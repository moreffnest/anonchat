package com.moreffnest.anonchat.repositories;

import com.moreffnest.anonchat.models.Conversation;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends ListCrudRepository<Conversation, Long> {
}
