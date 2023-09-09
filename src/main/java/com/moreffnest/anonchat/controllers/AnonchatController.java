package com.moreffnest.anonchat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moreffnest.anonchat.exceptions.ApplicationCancelledException;
import com.moreffnest.anonchat.exceptions.UserNotFoundException;
import com.moreffnest.anonchat.models.Application;
import com.moreffnest.anonchat.models.Conversation;
import com.moreffnest.anonchat.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Controller
@AllArgsConstructor
@Slf4j
public class AnonchatController {
    private final UserService userService;
    private final ConcurrentLinkedQueue<Application> userQueue;
    private final ConcurrentMap<Long, Conversation> activeConversations;
    private final Random conversationIdGenerator;
    private final ObjectMapper objectMapper;

    @GetMapping("")
    public String getStartPage(HttpServletRequest request) {
        System.out.println(request.getLocale());
        return "index";
    }


    @GetMapping("/chatform")
    public String chatform(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("isAuthenticated",
                auth.isAuthenticated()
                        && !(auth instanceof AnonymousAuthenticationToken)
        );
        return "chatform";
    }

    @PostMapping(value = "/search", consumes = "application/x-www-form-urlencoded")
    public String search(@RequestParam("your-sex") String applicantSex,
                         @RequestParam("interlocutor-sex") String interlocutorSex,
                         @RequestParam("your-age") int applicantAge,
                         @RequestParam("interlocutor-age-min") int ilAgeMin,
                         @RequestParam("interlocutor-age-max") int ilAgeMax,
                         Model model,
                         HttpServletRequest request)
            throws UserNotFoundException, IOException, ApplicationCancelledException {
        //save session to identify sender of the message, also adding new application removes the previous
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sessionId = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
        model.addAttribute("sessionId", sessionId);

        Application sameUserApplication = userQueue.stream()
                .filter(a -> a.getSessionId().equals(sessionId))
                .findAny().orElse(null);
        userQueue.remove(sameUserApplication);

        //create application; if user is authenticated, add User object to application
        Application application = new Application(null, applicantSex, applicantAge,
                interlocutorSex, new Application.IntRange(ilAgeMin, ilAgeMax), 0,
                sessionId);

        if (auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
            application.setApplicant(
                    userService.findByUsername(auth.getName())
                            .orElseThrow(() -> new UserNotFoundException("There is no user %s"
                                    .formatted(auth.getName())))
            );


        log.info("New application has been received: {}", application);

        //find matching application
        Application matchedApplication = userQueue.stream()
                .filter(a -> a.matches(application))
                .findAny().orElse(null);


        //if we found match, we should generate unique id for conversation
        if (matchedApplication != null) {
            userQueue.remove(matchedApplication);
            long id;
            do {
                id = conversationIdGenerator.nextLong() & Long.MAX_VALUE; //get only positive values
            } while (activeConversations.containsKey(id));

            //creating conversation and put it in active conversations
            //store conversations, if both users wants it
            // also add users if both authenticated
            Conversation conversation = new Conversation(id);
            if (application.getApplicant() != null && matchedApplication.getApplicant() != null) {
                conversation.setPreserved(application.getApplicant().isPreserveConversations()
                        && matchedApplication.getApplicant().isPreserveConversations());
                conversation.getUsers().addAll(
                        List.of(application.getApplicant(), matchedApplication.getApplicant()));
                conversation.findIntersectingTitles();
            }
            activeConversations.put(id, conversation);
            log.info("New conversation has started with id={} and users {}", id, conversation.getUsers());

            model.addAttribute("conversationId", id);
            //save intersected list types to model to turn on only necessary buttons
            model.addAttribute("intersectedListsTypes",
                    objectMapper.writeValueAsString(conversation.getIntersectingTitles().keySet()));
            matchedApplication.setConversationId(id);
        } else {    //match not found, waiting for other applications to come
            userQueue.add(application);
            try {
                while (application.getConversationId() == 0) {
                    Thread.sleep(2000);
                    System.out.println(1);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //if user cancelled his own application
            if (application.getConversationId() == -1)
                throw new ApplicationCancelledException("The application of the user with session id %s was cancelled"
                            .formatted(application.getSessionId()));
            //when match found add conversation id to model
            model.addAttribute("conversationId", application.getConversationId());
            //save intersected list types to model to turn on only necessary buttons
            model.addAttribute("intersectedListsTypes",
                    objectMapper.writeValueAsString(activeConversations.get(application.getConversationId())
                            .getIntersectingTitles().keySet()));
        }

        return "chat";
    }

    @DeleteMapping("/cancel")
    @ResponseBody
    public String cancel() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sessionId = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();

        userQueue.stream()
                .filter(a -> a.getSessionId().equals(sessionId))
                .findAny()
                .ifPresent(sameUserApplication -> sameUserApplication.setConversationId(-1));
        return "Application was cancelled successfully.";
    }


}
