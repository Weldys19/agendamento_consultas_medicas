package br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePatientDTO {

    private String name;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "O campo username não pode conter espaços")
    private String username;

    @Email(message = "O campo email deve conter um email válido")
    private String email;

    @Length(min = 10, message = "O campo password deve ter no mínimo 10 caracteres")
    private String password;
}
