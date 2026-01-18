package br.com.weldyscarmo.agendamento_consultas_medicas.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDTO {

    private String token;
    private Long expiresAt;
}
