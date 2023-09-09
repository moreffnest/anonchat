package com.moreffnest.anonchat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moreffnest.parsers.WebListParser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString(exclude = {"messages", "intersectingTitles", "users", "randomTopics"})
@JsonIgnoreProperties({"messages", "intersectingTitles", "users", "randomTopics"})
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSS")
    private LocalDateTime startDate;

    private boolean preserved;

    @OneToMany(targetEntity = Message.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private List<Message> messages;

    @Transient
    private Map<String, Set<WebListParser.Title>> intersectingTitles;

    @Transient
    private List<String> randomTopics;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_conversation",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;


    public Conversation(long id) {
        this.id = id;
        startDate = LocalDateTime.now();
        preserved = false;
        messages = new LinkedList<>();
        users = new LinkedList<>();
        intersectingTitles = new HashMap<>();

        randomTopics = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/static/txt/topics.txt")))
                .lines().collect(Collectors.toList());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Transactional
    public void findIntersectingTitles() {
        User user1 = users.get(0);
        User user2 = users.get(1);

        for (String listType : user1.getUserLists().keySet()) {
            if (user2.getUserLists().containsKey(listType)) {
                Set<WebListParser.Title> intersectingListTitles = new HashSet<>(user1.getUserLists().get(listType).getList());
                intersectingListTitles.retainAll(user2.getUserLists().get(listType).getList());
                if (intersectingListTitles.size() > 0)
                    intersectingTitles.put(listType, intersectingListTitles);
            }
        }
    }

    public WebListParser.Title getIntersectingTitle(String listType) {
        int size = intersectingTitles.get(listType).size();
        if (size == 0)
            return null;

        WebListParser.Title result = null;
        Iterator<WebListParser.Title> iterator = intersectingTitles.get(listType).iterator();
        for (int i = 0; i <= new Random().nextInt(size); i++) {
            result = iterator.next();
        }
        iterator.remove();
        return result;
    }

    public String randomTopic() {
        int size = randomTopics.size();
        if (size == 0)
            return null;

        String result = null;
        Iterator<String> iterator = randomTopics.iterator();
        for (int i = 0; i <= new Random().nextInt(size); i++)
            result = iterator.next();
        iterator.remove();
        return result;
    }
}
