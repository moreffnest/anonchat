package com.moreffnest.anonchat.repositories;

import com.moreffnest.anonchat.models.TitleList;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleListRepository extends ListCrudRepository<TitleList, Long> {
}
