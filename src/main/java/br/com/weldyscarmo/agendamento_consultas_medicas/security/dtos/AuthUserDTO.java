package br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDTO {

    private UUID id;
    private String email;
    private String password;
    private String roles;
}
