package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @Email(message = "Email не корректен")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}
