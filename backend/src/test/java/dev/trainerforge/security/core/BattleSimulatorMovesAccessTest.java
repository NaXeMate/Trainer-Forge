package dev.trainerforge.security.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.trainerforge.controller.BattleSimulatorController;
import dev.trainerforge.mapper.EntityReferenceMapper;
import dev.trainerforge.mapper.MoveMapperImpl;
import dev.trainerforge.mapper.TeamMapper;
import dev.trainerforge.model.entities.Move;
import dev.trainerforge.model.entities.PokemonType;
import dev.trainerforge.model.enumerated.MoveClass;
import dev.trainerforge.security.jwt.JwtFilter;
import dev.trainerforge.security.jwt.JwtUtil;
import dev.trainerforge.service.BattleSimulatorService;
import dev.trainerforge.service.MoveService;
import dev.trainerforge.service.TeamService;
import jakarta.servlet.Filter;

/** Exercises the move catalogue through the real controller, mapper and security chain. */
@SpringJUnitWebConfig(BattleSimulatorMovesAccessTest.TestConfiguration.class)
@TestPropertySource(properties = "trainerforge.cors.allowed-origins[0]=http://localhost:5173")
class BattleSimulatorMovesAccessTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter securityFilter;

    @Autowired
    private MoveService moveService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private BattleSimulatorService battleSimulatorService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(moveService, teamService, battleSimulatorService);
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilter).build();
    }

    @Test
    void anonymousCatalogueReturnsTheExistingMoveResponseFields() throws Exception {
        PokemonType fire = new PokemonType();
        fire.setName("Fire");
        Move flamethrower = new Move();
        flamethrower.setName("Flamethrower");
        flamethrower.setType(fire);
        flamethrower.setMoveClass(MoveClass.SPECIAL);
        flamethrower.setPower(90);
        flamethrower.setAccuracy(100);
        flamethrower.setPp(15);
        when(moveService.findAll()).thenReturn(List.of(flamethrower));

        mvc.perform(get("/api/simulator/moves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Flamethrower"))
                .andExpect(jsonPath("$[0].type").value("Fire"))
                .andExpect(jsonPath("$[0].moveClass").value("SPECIAL"))
                .andExpect(jsonPath("$[0].power").value(90))
                .andExpect(jsonPath("$[0].accuracy").value(100))
                .andExpect(jsonPath("$[0].pp").value(15));

        verify(moveService).findAll();
        verifyNoInteractions(teamService, battleSimulatorService);
    }

    @Test
    void emptyCatalogueReturnsAnEmptyList() throws Exception {
        when(moveService.findAll()).thenReturn(List.of());

        mvc.perform(get("/api/simulator/moves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void statusMovesKeepTheExistingDtosZeroDefaultsForAbsentPowerAndAccuracy() throws Exception {
        Move agility = new Move();
        agility.setName("Agility");
        agility.setMoveClass(MoveClass.STATUS);
        agility.setPower(null);
        agility.setAccuracy(null);
        when(moveService.findAll()).thenReturn(List.of(agility));

        mvc.perform(get("/api/simulator/moves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Agility"))
                .andExpect(jsonPath("$[0].moveClass").value("STATUS"))
                .andExpect(jsonPath("$[0].power").value(0))
                .andExpect(jsonPath("$[0].accuracy").value(0));
    }

    @Test
    void allowedFrontendOriginCanReadTheCatalogue() throws Exception {
        when(moveService.findAll()).thenReturn(List.of());

        mvc.perform(get("/api/simulator/moves").header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "PATCH", "DELETE"})
    void catalogueWriteRequestsStillRequireAuthentication(String method) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), URI.create("/api/simulator/moves")))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(moveService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/simulator/available-teams", "/api/simulator/battle?teamId1=1&teamId2=2"})
    void otherSimulatorEndpointsStillRequireAuthentication(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isUnauthorized());
        verifyNoInteractions(teamService, battleSimulatorService);
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, BattleSimulatorController.class, MoveMapperImpl.class})
    static class TestConfiguration {
        @Bean
        MoveService moveService() {
            return mock(MoveService.class);
        }

        @Bean
        TeamService teamService() {
            return mock(TeamService.class);
        }

        @Bean
        TeamMapper teamMapper() {
            return mock(TeamMapper.class);
        }

        @Bean
        BattleSimulatorService battleSimulatorService() {
            return mock(BattleSimulatorService.class);
        }

        @Bean
        EntityReferenceMapper entityReferenceMapper() {
            return mock(EntityReferenceMapper.class, Answers.CALLS_REAL_METHODS);
        }

        @Bean
        JwtFilter jwtFilter() {
            return new JwtFilter(mock(JwtUtil.class), mock(UserDetailsServiceImpl.class));
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper().registerModule(new JavaTimeModule());
        }
    }
}
