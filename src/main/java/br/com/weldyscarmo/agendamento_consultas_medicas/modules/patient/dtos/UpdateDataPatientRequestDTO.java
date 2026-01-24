package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDataPatientRequestDTO {

    @Schema(example = "weldys")
    private String name;

    @Pattern(regexp = "\\S+", message = "O campo username não pode conter espaços")
    @Schema(example = "weldyscarmo02")
    private String username;
}
