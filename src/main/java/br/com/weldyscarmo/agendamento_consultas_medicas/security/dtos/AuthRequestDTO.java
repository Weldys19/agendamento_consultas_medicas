package br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos;

import lombok.Data;

@Data
public class AuthRequestDTO {
        private String email;
        private String password;
}
