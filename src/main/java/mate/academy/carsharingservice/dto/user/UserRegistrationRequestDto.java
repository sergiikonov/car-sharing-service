package mate.academy.carsharingservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import mate.academy.carsharingservice.validator.FieldMatch;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords don't match")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Length(min = 2, max = 30)
    private String password;
    @NotBlank
    @Length(min = 2, max = 30)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
