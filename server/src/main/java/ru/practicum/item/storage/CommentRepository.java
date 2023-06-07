package ru.practicum.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.item.model.Comment;

import java.util.Collection;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findAllByItemId(long itemId);

    Collection<Comment> findAllByItemIdIn(Set<Long> itemIds);

}