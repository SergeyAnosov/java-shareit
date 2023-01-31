package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = " select * from items as i " +
            "where owner_id = ?1 " +
            "order by i.id", nativeQuery = true)
    List<Item> getOwnersItems(Long userId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> search(String text);
}
