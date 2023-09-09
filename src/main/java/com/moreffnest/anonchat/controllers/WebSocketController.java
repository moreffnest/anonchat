package com.moreffnest.anonchat.controllers;

import com.moreffnest.anonchat.models.Conversation;
import com.moreffnest.anonchat.models.Message;
import com.moreffnest.anonchat.repositories.ConversationRepository;
import com.moreffnest.parsers.WebListParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;

@Controller
@AllArgsConstructor
@Slf4j
public class WebSocketController {
    private ConcurrentMap<Long, Conversation> activeConversations;
    private ConversationRepository conversationRepository;

    @MessageMapping("/room/{conversationId}")
    @SendTo("/room/{conversationId}")
    public Message sendMessage(Message message,
                               @DestinationVariable long conversationId,
                               Principal principal) {
        message.setTime(LocalDateTime.now());

        //setting conversation and sender only if we want to store it in the database
        Conversation conversation = activeConversations.get(conversationId);
        if (conversation.isPreserved()) {
            message.setConversation(conversation);
            message.setSenderUsername(principal.getName());
            conversation.getMessages().add(message);
        }

        switch (message.getType()) {
            case SIMPLE -> {
                return message;
            }
            case DISCONNECT -> {
                if (conversation.isPreserved())
                    conversationRepository.save(conversation);
                activeConversations.remove(conversationId);
                log.info("Conversation with id={} ended", conversationId);
                return message;
            }
            case HELP -> {
                if (message.getContent().equals("RANDOM TOPIC")) {
                    String topic = conversation.randomTopic();
                    if (topic != null)
                        message.setContent(topic);
                    else message.setContent("there is no random topics left");
                } else {
                    WebListParser.Title title = conversation.getIntersectingTitle(message.getContent());
                    if (title != null)
                        message.setContent(title.getTitle());
                    else message.setContent("there is no " + message.getContent() + " titles left");
                }
                return message;
            }
            default -> {
                return null;
            }
        }
    }


}
