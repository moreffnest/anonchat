package com.moreffnest.anonchat.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String email;
    private String username;
    private String password;
}
