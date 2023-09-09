package com.moreffnest.anonchat.unit.controllers;

import com.moreffnest.anonchat.controllers.ProfileController;
import com.moreffnest.anonchat.unit.config.ProfileControllerTestConfig;
import com.moreffnest.anonchat.models.TitleList;
import com.moreffnest.anonchat.models.User;
import com.moreffnest.anonchat.repositories.TitleListRepository;
import com.moreffnest.anonchat.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ProfileController.class)
@Import(ProfileControllerTestConfig.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @Autowired
    public ProfileControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser("startuser")
    void changeUser(@Autowired UserRepository userRepository) throws Exception {
        User startUser = new User("startuser", "startuser@gmail.com", "startpwd");
        startUser.setFirstName("Phillip");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(startUser));


        this.mockMvc.perform(
                        post("/profile/account/change")
                                .contentType("application/x-www-form-urlencoded")
                                .param("username", "newuser")
                                .param("email", "newuser@gmail.com")
                                .param("firstName", "Trevor")
                                .param("lastName", "0114")
                                .param("preserveConversations", "true")
                                .param("dateOfBirth", "2000-08-31")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        assertEquals("newuser", startUser.getUsername());
        assertEquals("newuser@gmail.com", startUser.getEmail());
        assertEquals("Trevor", startUser.getFirstName());
        assertEquals("0114", startUser.getLastName());
        assertTrue(startUser.isPreserveConversations());
        assertEquals(LocalDate.of(2000, Month.AUGUST, 31), startUser.getDateOfBirth());
    }

    @Test
    @WithMockUser(username = "startuser")
    void changePassword(@Autowired PasswordEncoder passwordEncoder,
                        @Autowired UserRepository userRepository) throws Exception {
        User startUser = new User("startuser", "startuser@gmail.com",
                "$2a$07$lOCpxIv2jaa8OtaFF5VxaOgezA8CV2FvdPp4dyalSTm5uaupWQujm");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(startUser));

        this.mockMvc.perform(
                        post("/profile/security/change_password")
                                .param("oldPassword", "startpwd")
                                .param("newPassword", "hehepass")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "startuser")
    void changePasswordFail(@Autowired PasswordEncoder passwordEncoder,
                            @Autowired UserRepository userRepository) throws Exception {
        User startUser = new User("startuser", "startuser@gmail.com",
                "$2a$07$lOCpxIv2jaa8OtaFF5VxaOgezA8CV2FvdPp4dyalSTm5uaupWQujm");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(startUser));

        this.mockMvc.perform(
                        post("/profile/security/change_password")
                                .param("oldPassword", "notstartpwd")
                                .param("newPassword", "hehepass")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @WithMockUser
    void uploadLists(@Autowired UserRepository userRepository,
                     @Autowired TitleListRepository titleListRepository) throws Exception {
        User startUser = new User("startuser", "startuser@gmail.com",
                "$2a$07$lOCpxIv2jaa8OtaFF5VxaOgezA8CV2FvdPp4dyalSTm5uaupWQujm");
        Map<String, TitleList> userLists = startUser.getUserLists();
        userLists.put("YOUTUBE", new TitleList());
        userLists.put("IMDB", new TitleList());

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(startUser));

        MockMultipartFile file
                = new MockMultipartFile(
                "yt-list",
                "hello.html",
                MediaType.TEXT_HTML_VALUE,
                "<!DOCTYPE html><html></html>!".getBytes()
        );

        this.mockMvc.perform(
                        multipart("/profile/lists/upload")
                                .file(file)
                                .param("list-type", "IMDB")
                                .param("list-link", "https://www.imdb.com/list/ls561225322/")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(titleListRepository, times(2)).delete(any(TitleList.class));
        verify(userRepository).save(any(User.class));
    }
}