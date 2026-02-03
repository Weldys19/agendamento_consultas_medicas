package br.com.weldyscarmo.agendamento_consultas_medicas.security.controller;

import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.AuthRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.TokenResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.useCases.AuthUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação de usuário")
public class AuthController {

    @Autowired
    private AuthUserUseCase authUserUseCase;

    @Operation(summary = "Autenticar usuário",
            description = "Essa função é responsável pela autenticação de usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = TokenResponseDTO.class))
            }),
            @ApiResponse(responseCode = "401", description = "Email/Senha inválido")
    })
    @PostMapping("/")
    public ResponseEntity<TokenResponseDTO> create(@RequestBody AuthRequestDTO authRequestDTO){
        TokenResponseDTO result = this.authUserUseCase.execute(authRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
