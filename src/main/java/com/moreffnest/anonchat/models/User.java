package com.moreffnest.anonchat.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"userConversations", "userRoles", "userLists"})
@Table(name = "chatuser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String username;
    @Column(name = "birth_date")
    private LocalDate dateOfBirth;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    private String password;
    @Column(columnDefinition = "boolean default false")
    private boolean preserveConversations;


    @ManyToMany(mappedBy = "users")
    List<Conversation> userConversations;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    List<Role> userRoles;

    @OneToMany(mappedBy = "user", targetEntity = TitleList.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapKey(name = "listType")
    Map<String, TitleList> userLists;


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRoles = new LinkedList<>();
        this.userLists = new HashMap<>();
    }

}
