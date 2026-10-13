package dev.trainerforge.security.core;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.Filter;

@SpringJUnitWebConfig
@ContextConfiguration(classes = PokedexPublicAccessTest.TestConfiguration.class,
        initializers = PokedexPublicAccessTest.ApplicationProperties.class)
@TestPropertySource(properties =
        "CORS_ALLOWED_ORIGINS=https://trainerforge.example,https://preview.trainerforge.example")
class PokedexCorsOverrideTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter securityFilter;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilter).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://trainerforge.example", "https://preview.trainerforge.example"})
    void configuredOriginsPassThePreflight(String origin) throws Exception {
        mvc.perform(options("/api/pokedex")
                .header("Origin", origin)
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", origin));
    }

    @Test
    void customOriginsReplaceTheLocalDefaults() throws Exception {
        mvc.perform(options("/api/pokedex")
                .header("Origin", "http://127.0.0.1:5174")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
