package ru.practicum.shareitserver.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.booking.BookingController;
import ru.practicum.shareitserver.booking.BookingStatus;
import ru.practicum.shareitserver.booking.dto.BookingDtoResponse;
import ru.practicum.shareitserver.booking.dto.BookingMappers;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerIT {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User user;
    private Item item;
    private Booking booking;
    private BookingDtoResponse response;
    private NotFoundException notFoundException = new NotFoundException(HttpStatus.NOT_FOUND, "на найдено");

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@ya.ru");
        item = new Item(1L, "Вещь1", "Описание", true,
                user, 1L);
        booking = new Booking(1L, LocalDateTime.of(2024, 1, 2, 3, 20, 10),
                                      LocalDateTime.of(2024, 1, 2, 5, 20, 10),
        item, user, BookingStatus.WAITING);
        response = BookingMappers.toBookingDtoResponse(booking);
    }

    @SneakyThrows
    @Test
    void create() {
        when(bookingService.create(any(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @SneakyThrows
    @Test
    void approveBooking() {
        response.setStatus(BookingStatus.APPROVED);

        when(bookingService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(response);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @SneakyThrows
    @Test
    void findById() {
        when(bookingService.findById(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));

    }

    @SneakyThrows
    @Test
    void findAllByUser() {
        when(bookingService.findAllByUser(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getAllForUser() {
        when(bookingService.findAllByOwner(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}