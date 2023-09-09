package com.moreffnest.anonchat.intergration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = "/create-request.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-request.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginTest {
    private MockMvc mockMvc;

    @Autowired
    public LoginTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void redirectUnauthenticated() throws Exception {
        this.mockMvc.perform(
                get("/profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void loginSuccess() throws Exception {
        this.mockMvc.perform(
                formLogin("/perform-login").user("justuser").password("justuser"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatform"));
    }

    @Test
    public void loginFailure() throws Exception {
        this.mockMvc.perform(
                formLogin("/perform-login").user("justuser").password("incorrect"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

    }

}
