package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentsResponseDTO {

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentsStatus status;
    private LocalDateTime createdAt;
}
