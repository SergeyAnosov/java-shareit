package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
public class ItemDto {
    private long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private long requestId;

    public ItemDto(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
