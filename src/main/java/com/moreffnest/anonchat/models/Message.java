package com.moreffnest.anonchat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String senderSessionId;
    private String senderUsername;
    private MessageType type;

    @NonNull
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSS")
    private LocalDateTime time;

    @ManyToOne(cascade = CascadeType.ALL)
    private Conversation conversation;

}
