package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorScheduleResponseDTO {

    private UUID id;
    private DoctorEntity doctorEntity;
    private UUID doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
