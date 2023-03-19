package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestTest {
    @Autowired
    JacksonTester<ItemRequestDto> jtItemRequestDto;
    @Autowired
    JacksonTester<ItemRequestWithItemsDto> jtItemRequestDtoWithItems;

    @Test
    public void itemRequestDtoJsonTest() throws IOException {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Описание", 1L, LocalDateTime.of(2023, 10, 10, 10, 10, 10));

        JsonContent<ItemRequestDto> requestDtoResult = jtItemRequestDto.write(requestDto);

        assertThat(requestDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(requestDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
    }

    @Test
    public void itemRequestDtoWithItems() throws IOException {
        ItemRequestWithItemsDto requestDto = new ItemRequestWithItemsDto(
                1L,
                "Описание",
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                null
        );

        JsonContent<ItemRequestWithItemsDto> requestDtoResult = jtItemRequestDtoWithItems.write(requestDto);

        assertThat(requestDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(requestDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
        assertThat(requestDtoResult).extractingJsonPathValue("$.created").isEqualTo(requestDto.getCreated().toString());
        assertThat(requestDtoResult).extractingJsonPathValue("$.items").isNull();
    }
}
