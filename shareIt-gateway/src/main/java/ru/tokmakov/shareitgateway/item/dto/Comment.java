package ru.tokmakov.shareitgateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.tokmakov.shareitgateway.user.dto.User;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Comment {
    private long id;
    @NotBlank
    private String text;

    private Item item;

    private User user;

    private LocalDate created;
}
