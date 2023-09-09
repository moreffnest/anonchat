package com.moreffnest.anonchat.unit.models;

import com.moreffnest.anonchat.models.Conversation;
import com.moreffnest.anonchat.models.TitleList;
import com.moreffnest.anonchat.models.User;
import com.moreffnest.parsers.WebListParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationTest {
    private Conversation conversation;
    @Mock
    private List<User> users;

    @BeforeEach
    void сonversationTest() {
        conversation = new Conversation();
        conversation.setUsers(users);
        conversation.setIntersectingTitles(new HashMap<>());
    }

    @Test
    void findAndGetIntersectingTitles() {
        User user1 = new User();
        user1.setUserLists(new HashMap<>());
        user1.getUserLists().put("IMDB",
                new TitleList("IMDB", Set.of(new WebListParser.Title("Game of Thrones", "https://www.imdb.com/title/tt0944947"),
                        new WebListParser.Title("Big Bang Theory", "https://www.imdb.com/title/tt0898266"),
                        new WebListParser.Title("Breaking Bad", "https://www.imdb.com/title/tt0903747")), user1));
        User user2 = new User();
        user2.setUserLists(new HashMap<>());
        user2.getUserLists().put("IMDB",
                new TitleList("IMDB", Set.of(new WebListParser.Title("Cloud-Paradise", "https://www.imdb.com/title/tt0102574/?ref_=ttls_li_tt"),
                        new WebListParser.Title("Breaking Bad", "https://www.imdb.com/title/tt0903747"),
                        new WebListParser.Title("Game of Thrones", "https://www.imdb.com/title/tt0944947")), user2));


        when(users.get(0)).thenReturn(user1);
        when(users.get(1)).thenReturn(user2);


        Set<WebListParser.Title> expected = Set.of(new WebListParser.Title("Game of Thrones", "https://www.imdb.com/title/tt0944947"),
                new WebListParser.Title("Breaking Bad", "https://www.imdb.com/title/tt0903747"));
        conversation.findIntersectingTitles();

        assertThat("hasAllItems", conversation.getIntersectingTitles().get("IMDB"),
                containsInAnyOrder(expected.toArray()));
        assertThat("hasArbitraryItem", expected,
                hasItem(conversation.getIntersectingTitle("IMDB")));
    }

}