package ar.com.itau.seed.adapter.controller;

import ar.com.itau.seed.application.port.in.GetSWCharacterByIdQuery;
import ar.com.itau.seed.application.port.out.UserRepository;
import ar.com.itau.seed.config.TestConfig;
import ar.com.itau.seed.config.TestSecurityConfig;
import ar.com.itau.seed.mock.SWCharacterMockFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SWCharacter Adapter Test")
@WebMvcTest(SWCharacterControllerAdapter.class)
@Import({TestConfig.class, TestSecurityConfig.class})
public class SWCharacterControllerAdapterTest {

    private static final int SW_CHARACTER_ID = 4;
    private static final String CHARACTERS_URL = "/api/v1/characters";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetSWCharacterByIdQuery getSWCharacterByIdQuery;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("when get is called the adapter should return the character it found")
    void testGetSWCharacterByIdSuccessfully() throws Exception {
        final String expected = objectMapper.writeValueAsString(SWCharacterMockFactory.getSWCharacterControllerModel());

        Mockito.when(getSWCharacterByIdQuery.get(Mockito.eq(SW_CHARACTER_ID)))
                .thenReturn(CompletableFuture.completedFuture(SWCharacterMockFactory.getSWCharacter()));

        final MvcResult result = mockMvc.perform(get(buildGetUrl())).andReturn();
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @DisplayName("when get is called with an invalid id the adapter should return a Bad Request")
    void testGetSWCharacterByIdWithAnInvalidId() throws Exception {
        final String expected = "{ \"name\": \"Bad Request\", " +
                "\"description\": \"get.id: must be greater than 0\", " +
                "\"code\": \"PRE-FIX:101\" }";

        mockMvc.perform(get(buildInvalidGetUrl()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    private String buildGetUrl() {
        return CHARACTERS_URL + "/" + SW_CHARACTER_ID;
    }

    private String buildInvalidGetUrl() {
        return CHARACTERS_URL + "/-1";
    }

}
