package com.moreffnest.anonchat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.moreffnest.anonchat.dto.PasswordDTO;
import com.moreffnest.anonchat.models.User;
import com.moreffnest.anonchat.models.Conversation;
import com.moreffnest.anonchat.models.Message;
import com.moreffnest.anonchat.models.TitleList;
import com.moreffnest.anonchat.repositories.ConversationRepository;
import com.moreffnest.anonchat.repositories.TitleListRepository;
import com.moreffnest.anonchat.services.UserService;
import com.moreffnest.parsers.WebListParser;
import com.moreffnest.parsers.YoutubeHistoryParser;
import com.moreffnest.parsers.exceptions.InvalidFileExtensionException;
import com.moreffnest.parsers.exceptions.InvalidListPageException;
import com.moreffnest.parsers.exceptions.InvalidListTypeException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
@Slf4j
public class ProfileController {
    private final UserService userService;
    private final TitleListRepository titleListRepository;
    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;


    @GetMapping("")
    public String profileStartPage() {
        return "profile/profile";
    }

    @GetMapping("/account")
    public String getAccountChangePage(Model model, Principal principal) {
        model.addAttribute("user", new User());
        model.addAttribute("currentUser",
                userService.findByUsername(principal.getName()).get());
        return "profile/profile_account";
    }

    @GetMapping("/security")
    public String getSecurityPage(Model model) {
        model.addAttribute("passwords", new PasswordDTO());
        return "profile/profile_security";
    }

    @GetMapping("/lists")
    public String getListsPage() {
        return "profile/profile_lists";
    }

    @GetMapping("/conversations")
    public String getConversationsPage(Model model, Principal principal) throws JsonProcessingException {
        List<Conversation> userConversations = userService.findByUsername(principal.getName())
                .get().getUserConversations();
        model.addAttribute("conversations",
                objectMapper.writeValueAsString(userConversations));
        return "profile/profile_conversations";
    }

    @GetMapping("conversations/{conversationId}")
    public String getConversationMessages(@PathVariable long conversationId,
                                          Principal principal,
                                          Model model)
            throws JsonProcessingException {

        model.addAttribute("username", principal.getName());
        Conversation conversation = conversationRepository.findById(conversationId).get();
        List<Message> messages = conversation.getMessages();
        //check if user is participant of the conversation
        if (conversation.getUsers().stream()
                .map(User::getUsername)
                .anyMatch(username -> username.equals(principal.getName())))
            model.addAttribute("messages",
                    objectMapper.writeValueAsString(messages));
        return "profile/profile_specific_conversation";
    }

    @PostMapping("/account/change")
    public String changeUser(@ModelAttribute User user, Principal principal) {
        userService.changeUser(principal.getName(), user);
        //set new auth with new username
        if (user.getUsername() != null && !user.getUsername().equals("")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, auth.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        return "redirect:/profile/account";
    }

    @PostMapping("/security/change_password")
    public RedirectView changePassword(@ModelAttribute PasswordDTO passwords, Principal principal,
                                 RedirectAttributes attributes) {

        if (!userService.changePassword(principal.getName(), passwords))
            attributes.addAttribute("message", "error");
        else
            attributes.addAttribute("message", "success");
        return new RedirectView("/profile/security");
    }

    @PostMapping("/lists/upload")
    @Transactional
    public String uploadLists(@RequestParam(name="yt-list", required = false) MultipartFile ytHistory,
                              @RequestParam(name="list-link", required = false) String listUrl,
                              @RequestParam(name="list-type", required = false) String listType,
                              RedirectAttributes attributes,
                              Principal principal) throws IOException, InvalidFileExtensionException, InvalidListTypeException, InvalidListPageException {
        User user = userService.findByUsername(principal.getName()).get(); //only authenticated users here

        if (!ytHistory.isEmpty()) {
            //get file extension
            if (!Files.getFileExtension(ytHistory.getOriginalFilename()).equals("html"))
                throw new InvalidFileExtensionException("The file has not .html extension!");

            Set<YoutubeHistoryParser.YoutubeVideo> ytVideos =
                YoutubeHistoryParser.parse(ytHistory.getInputStream(), YoutubeHistoryParser.FileType.HTML);
            Set<WebListParser.Title> ytWebList = WebListParser.mapYoutubeVideoToTitle(ytVideos);
            //delete previous youtube list if exists
            if (user.getUserLists().containsKey("YOUTUBE"))
                titleListRepository.delete(user.getUserLists().get("YOUTUBE"));
            user.getUserLists().put("YOUTUBE", new TitleList("YOUTUBE", ytWebList, user));

            log.info("User {} uploaded YouTube list", principal.getName());
        }

        if (listUrl != null && !listUrl.equals("")) {
            Set<WebListParser.Title> webList = WebListParser.parse(listUrl);
            //delete previous list of this type if exists
            if (user.getUserLists().containsKey(listType))
                titleListRepository.delete(user.getUserLists().get(listType));
            user.getUserLists().put(listType, new TitleList(listType, webList, user));

            log.info("User {} uploaded {} list", principal.getName(), listType);
        }
        if (!ytHistory.isEmpty() || (listUrl != null && !listUrl.equals("")))
            userService.save(user);

        attributes.addAttribute("message", "success");
        return "redirect:/profile/lists";
    }

}


