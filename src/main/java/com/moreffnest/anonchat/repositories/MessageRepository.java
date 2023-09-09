package com.moreffnest.anonchat.repositories;

import com.moreffnest.anonchat.models.Message;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ListCrudRepository<Message, Long> {
}
