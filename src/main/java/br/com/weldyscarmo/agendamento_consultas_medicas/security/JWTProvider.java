package br.com.weldyscarmo.agendamento_consultas_medicas.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JWTProvider {

    public TokenResponseDTO generateToken(AuthUser authUser){

        var expiresAt = Instant.now().plus(Duration.ofMinutes(15));
        var algorithm = Algorithm.HMAC256("@Weldys2025");

        var token = JWT.create().withIssuer("agendamento-medico")
                .withSubject(authUser.getId().toString())
                .withClaim("roles", authUser.getRoles())
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return TokenResponseDTO.builder()
                .token(token)
                .expiresAt(expiresAt.toEpochMilli())
                .build();
    }
}
