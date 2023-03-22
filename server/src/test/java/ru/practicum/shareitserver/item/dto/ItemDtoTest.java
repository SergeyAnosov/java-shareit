package ru.practicum.shareitserver.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jtItemDto;
    @Autowired
    private JacksonTester<ItemInfoDto> jtItemDtoWithBooking;

    @Test
    public void itemDtoJsonTest() throws IOException {
        ItemDto itemDto = new ItemDto(1L, "Название", "Описание", true, 1L);

        JsonContent<ItemDto> itemDtoResult = jtItemDto.write(itemDto);

        assertThat(itemDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(itemDtoResult).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoResult).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(itemDtoResult).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());
    }

    @Test
    public void itemDtoWithBookingJsonTest() throws IOException {
        ItemInfoDto itemDto = new ItemInfoDto(
                1L,
                "Название",
                "Описание",
                true,
                null,
                null,
                null
               );

        JsonContent<ItemInfoDto> itemDtoResult = jtItemDtoWithBooking.write(itemDto);

        assertThat(itemDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(itemDtoResult).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoResult).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(itemDtoResult).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(itemDtoResult).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(itemDtoResult).extractingJsonPathArrayValue("$.comments").isEqualTo(itemDto.getComments());
    }
}
