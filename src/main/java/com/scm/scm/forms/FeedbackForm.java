package com.scm.scm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FeedbackForm {

    // @NotBlank(message = "Name is required")
    // private String name;

    // @NotBlank(message = "Email is required")
    // @Email(message = "Invalid Email Address [ example@gmail.com ]")
    // private String email;

    @Size(max = 20, message = "Max 20 characters are allowed")
    public String subject;
    private String message;

    private int rating;
}
