package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.utils;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.PatientResponseDTO;

public class MapperPatientResponseDTO {

    public static PatientResponseDTO mapperPatientResponse(PatientEntity patientEntity){
        return PatientResponseDTO.builder()
                .id(patientEntity.getId())
                .name(patientEntity.getName())
                .username(patientEntity.getUsername())
                .email(patientEntity.getEmail())
                .createdAt(patientEntity.getCreatedAt())
                .build();
    }
}
