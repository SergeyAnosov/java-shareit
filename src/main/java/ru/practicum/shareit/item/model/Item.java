package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Item {
    private long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean available;
    @NotNull
    private User owner;
    private ItemRequest request;

    public Item(String name, String description, Boolean available, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
