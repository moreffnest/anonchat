package com.moreffnest.anonchat.controllers;

import com.moreffnest.anonchat.models.Application;
import com.moreffnest.anonchat.models.Conversation;
import com.moreffnest.anonchat.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Controller
@AllArgsConstructor
public class UtilController {
    private final UserService userService;
    private final ConcurrentLinkedQueue<Application> userQueue;
    private ConcurrentMap<Long, Conversation> activeConversations;

    @GetMapping("/usersnumber")
    @ResponseBody
    public int getUsersNumber() {
        return activeConversations.size()*2;
    }

    @GetMapping("/checkusername")
    @ResponseBody
    public boolean isUsernameTaken(@RequestParam String username) {
        return userService.findByUsername(username).isPresent();
    }
    @GetMapping("/checkemail")
    @ResponseBody
    public boolean isEmailTaken(@RequestParam String email) {
        return userService.findByEmail(email).isPresent();
    }


}
