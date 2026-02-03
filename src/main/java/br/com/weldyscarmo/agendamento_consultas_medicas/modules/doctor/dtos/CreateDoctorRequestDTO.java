package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Schema(example = "Weldys Carmo")
    private String name;

    @NotBlank
    @Schema(example = "Cirurgião")
    private String specialty;

    @NotNull
    @Schema(example = "60")
    private Long consultationDurationInMinutes;

    @Email(message = "O campo email deve conter um email válido")
    @Schema(example = "weldys@gmail.com")
    private String email;

    @Length(min = 8, message = "O campo password deve ter no mínimo 8 caracters")
    @Schema(example = "12345678")
    private String password;
}
