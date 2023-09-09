package com.moreffnest.anonchat.models;

import com.moreffnest.anonchat.util.TitleSetConverter;
import com.moreffnest.parsers.WebListParser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TitleList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String listType;
    @Lob
    @Convert(converter = TitleSetConverter.class)
    private Set<WebListParser.Title> list;
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public TitleList(String listType, Set<WebListParser.Title> list, User user) {
        this.listType = listType;
        this.list = list;
        this.user = user;
    }

}
