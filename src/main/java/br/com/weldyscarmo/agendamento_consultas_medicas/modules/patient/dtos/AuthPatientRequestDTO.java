package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos;

import lombok.Data;

@Data
public class AuthPatientRequestDTO {

    private String email;
    private String password;
}
