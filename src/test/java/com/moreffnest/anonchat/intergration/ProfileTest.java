package com.moreffnest.anonchat.intergration;

import com.moreffnest.anonchat.models.User;
import com.moreffnest.anonchat.repositories.UserRepository;
import com.moreffnest.anonchat.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("justuser")
@TestPropertySource("/application-test.properties")
@Sql(value = "/create-request.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-request.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ProfileTest {
    private MockMvc mockMvc;

    @Autowired
    public ProfileTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @Test
    public void testPage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(content().string(containsString("welcome to our anonymous chat")));
    }

    @Test
    public void changeAccountInfo(@Autowired UserRepository userRepository) throws Exception {
        this.mockMvc.perform(
                post("/profile/account/change")
                        .param("username", "nojustuser")
                        .param("email", "somemail@gmail.com")
                        .param("firstName", "bob")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/account"));

        Optional<User> userOptional = userRepository.findByUsername("nojustuser");
        assertTrue(userOptional.isPresent());
        assertEquals("bob", userOptional.get().getFirstName());
        assertEquals("somemail@gmail.com", userOptional.get().getEmail());
    }

    @Test
    public void checkConversationMessages() throws Exception {
        this.mockMvc.perform(get("/profile/conversations/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hello!")))
                .andExpect(content().string(containsString("good. bye.")));
    }
}
