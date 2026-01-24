package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDoctorScheduleRequestDTO {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    @Schema(example = "09:00")
    private LocalTime startTime;

    @NotNull
    @Schema(example = "18:00")
    private LocalTime endTime;
}
