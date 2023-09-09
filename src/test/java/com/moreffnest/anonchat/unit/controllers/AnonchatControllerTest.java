package com.moreffnest.anonchat.unit.controllers;

import com.moreffnest.anonchat.controllers.AnonchatController;
import com.moreffnest.anonchat.unit.config.AnonchatControllerTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AnonchatController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(AnonchatControllerTestConfig.class)
class AnonchatControllerTest {

    private MockMvc mockMvc;

    @Autowired
    AnonchatControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void search() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        WebAuthenticationDetails webAuthenticationDetails = mock(WebAuthenticationDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
        when(webAuthenticationDetails.getSessionId()).thenReturn("M0CKS3SS10N");

        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(
                        post("/search")
                                .contentType("application/x-www-form-urlencoded")
                                .param("your-sex", "male")
                                .param("interlocutor-sex", "no")
                                .param("your-age", "24")
                                .param("interlocutor-age-min", "21")
                                .param("interlocutor-age-max", "30")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//div//p[@id='session_id']").string("M0CKS3SS10N"));
    }
}