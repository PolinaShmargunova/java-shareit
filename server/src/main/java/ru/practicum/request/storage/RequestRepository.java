package ru.practicum.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequestorId(long userId);

    List<ItemRequest> findAllByRequestorIdIsNot(Long userId, Pageable page);

}