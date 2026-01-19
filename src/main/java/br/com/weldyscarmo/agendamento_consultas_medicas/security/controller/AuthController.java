package br.com.weldyscarmo.agendamento_consultas_medicas.security.controller;

import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.AuthRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.AuthUserDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.TokenResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.useCases.AuthUserUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthUserUseCase authUserUseCase;

    @PostMapping("/")
    public ResponseEntity<TokenResponseDTO> create(@RequestBody AuthRequestDTO authRequestDTO){
        var result = this.authUserUseCase.execute(authRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
