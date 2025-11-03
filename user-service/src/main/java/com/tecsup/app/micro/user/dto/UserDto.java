package com.tecsup.app.micro.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private LocalDateTime createdAt;

    //private LocalDateTime updatedAt;

}
