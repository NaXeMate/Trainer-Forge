package dev.trainerforge.security.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
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
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.trainerforge.controller.PokedexController;
import dev.trainerforge.mapper.EntityReferenceMapper;
import dev.trainerforge.mapper.PokedexMapperImpl;
import dev.trainerforge.model.entities.Pokedex;
import dev.trainerforge.model.entities.PokemonType;
import dev.trainerforge.model.enumerated.PokemonClass;
import dev.trainerforge.security.jwt.JwtFilter;
import dev.trainerforge.security.jwt.JwtUtil;
import dev.trainerforge.service.PokedexService;
import jakarta.servlet.Filter;

/** Exercises the real controller, generated mapper, JWT filter and security chain without a database. */
@SpringJUnitWebConfig
@ContextConfiguration(classes = PokedexPublicAccessTest.TestConfiguration.class,
        initializers = PokedexPublicAccessTest.ApplicationProperties.class)
class PokedexPublicAccessTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter securityFilter;

    @Autowired
    private PokedexService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(service);
        Pokedex species = new Pokedex();
        species.setId(42L);
        species.setNationalPokedex(152L);
        species.setName("Chikorita");
        species.setImageUrl("https://example.com/chikorita.png");
        species.setPokemonClass(PokemonClass.COMMON);
        PokemonType grass = new PokemonType();
        grass.setName("Grass");
        species.setType1(grass);
        when(service.findAll()).thenReturn(List.of(species));
        when(service.findById(42L)).thenReturn(species);
        when(service.findByName("Chikorita")).thenReturn(species);
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilter).build();
    }

    @Test
    void anonymousListIncludesDatabaseIdentityAndCardFields() throws Exception {
        mvc.perform(get("/api/pokedex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(42))
                .andExpect(jsonPath("$[0].nationalPokedex").value(152))
                .andExpect(jsonPath("$[0].name").value("Chikorita"))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/chikorita.png"))
                .andExpect(jsonPath("$[0].type1").value("Grass"))
                .andExpect(jsonPath("$[0].type2").isEmpty())
                .andExpect(jsonPath("$[0].pokemonClass").value("COMMON"));
    }

    @Test
    void anonymousDetailIncludesTheSameDatabaseIdentity() throws Exception {
        mvc.perform(get("/api/pokedex/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.nationalPokedex").value(152));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/api/pokedex/name/Chikorita", "/api/pokedex/number/152", "/api/pokedex/type/12",
        "/api/pokedex/generation/2", "/api/pokedex/region/2", "/api/pokedex/ability/134",
        "/api/pokedex/class/LEGENDARY", "/api/pokedex/stats/hp?min=0&max=255",
        "/api/pokedex/stats/attack?min=0&max=255", "/api/pokedex/stats/defense?min=0&max=255",
        "/api/pokedex/stats/special-attack?min=0&max=255",
        "/api/pokedex/stats/special-defense?min=0&max=255", "/api/pokedex/stats/speed?min=0&max=255"
    })
    void anonymousPokedexQueriesRemainAvailable(String path) throws Exception {
        mvc.perform(get(URI.create(path))).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "PATCH", "DELETE"})
    void anonymousWritesStillRequireAuthentication(String method) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), "/api/pokedex/42"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/trainers", "/api/teams", "/api/pokemon", "/api/pokedex-other"})
    void otherApiRoutesStillRequireAuthentication(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://localhost:3000", "http://localhost:5173", "http://localhost:5174",
        "http://127.0.0.1:3000", "http://127.0.0.1:5173", "http://127.0.0.1:5174"
    })
    void configuredLocalOriginsPassTheBrowserPreflight(String origin) throws Exception {
        mvc.perform(options("/api/pokedex")
                .header("Origin", origin)
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", origin));
    }

    @Test
    void crossOriginGetReturnsBothTheDataAndTheCorsHeader() throws Exception {
        mvc.perform(get("/api/pokedex").header("Origin", "http://127.0.0.1:5174"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:5174"))
                .andExpect(jsonPath("$[0].id").value(42));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "http://localhost:5175", "http://127.0.0.1:5174.evil.test"})
    void unlistedOriginsAreRejected(String origin) throws Exception {
        mvc.perform(options("/api/pokedex")
                .header("Origin", origin)
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    public static class ApplicationProperties implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource("application.yml"));
            context.getEnvironment().getPropertySources().addLast(
                    new PropertiesPropertySource("application", yaml.getObject()));
        }
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, PokedexController.class, PokedexMapperImpl.class})
    static class TestConfiguration {
        @Bean
        PokedexService pokedexService() {
            return mock(PokedexService.class);
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
