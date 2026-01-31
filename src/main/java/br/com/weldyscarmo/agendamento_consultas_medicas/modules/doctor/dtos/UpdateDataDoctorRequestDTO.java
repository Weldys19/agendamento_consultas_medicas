package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDataDoctorRequestDTO {

    private String name;
    private String speciality;
    private Long consultationDurationInMinutes;
}
