package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDoctorRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String specialty;

    @Email(message = "O campo email deve conter um email válido")
    private String email;

    @Length(min = 8, message = "O campo password deve ter no mínimo 8 caracters")
    private String password;
}
