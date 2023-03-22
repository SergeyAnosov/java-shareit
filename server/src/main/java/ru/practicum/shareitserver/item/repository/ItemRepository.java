package ru.practicum.shareitserver.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareitserver.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = " select * from items as i " +
            "where owner_id = ?1 " +
            "order by i.id", nativeQuery = true)
    Page<Item> getOwnersItems(Long userId, PageRequest pageRequest);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    Page<Item> search(String text, PageRequest pageRequest);

    List<Item> findByRequestId(Long requestId);
}
