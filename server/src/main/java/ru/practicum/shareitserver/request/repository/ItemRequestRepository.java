package ru.practicum.shareitserver.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreated(Long requesterId);

    Page<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(PageRequest pageRequest, Long requesterId);
}
