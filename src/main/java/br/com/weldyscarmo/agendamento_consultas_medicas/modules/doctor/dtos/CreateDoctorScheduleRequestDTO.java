package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;


import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class CreateDoctorScheduleRequestDTO {

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
