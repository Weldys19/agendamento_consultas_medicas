package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorTimeBlockResponseDTO {

    private UUID id;
    private UUID doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
