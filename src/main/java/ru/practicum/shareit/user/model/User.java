package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private Long id;
    @NotBlank
    private String name;
    @Email(message = "Email не корректен")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

}
