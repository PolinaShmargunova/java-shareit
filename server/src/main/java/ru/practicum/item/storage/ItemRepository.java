package ru.practicum.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item as i " +
            "where i.available = true " +
            "and (lower(i.name) like lower (concat('%', ?1, '%')) " +
            "or lower(i.description) like lower (concat('%', ?1, '%')))")
    List<Item> search(String text, Pageable page);

    List<Item> findAllByOwnerId(Long owner, Pageable page);

    Collection<Item> findAllByRequestRequestorId(Long requestorId);

    Collection<Item> findAllByRequestId(Long requestId);

}