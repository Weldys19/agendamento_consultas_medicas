package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO {

    private UUID id;
    private String name;
    private String specialty;
    private Long consultationDurationInMinutes;
    private String email;
    private LocalDateTime createdAt;
}
