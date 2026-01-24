package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.CreateDoctorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Informações do médico")
public class DoctorController {

    @Autowired
    private CreateDoctorUseCase createDoctorUseCase;

    @Operation(summary = "Cadastrar médico",
            description = "Essa função é responsável pelo cadastramento de médicos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = CreateDoctorResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Usuário já existe", content = {
                    @Content(schema = @Schema(implementation = String.class))
            })
    })
    @PostMapping("/")
    public ResponseEntity<CreateDoctorResponseDTO> create(@Valid @RequestBody CreateDoctorRequestDTO createDoctorRequestDTO){
        CreateDoctorResponseDTO result = this.createDoctorUseCase.execute(createDoctorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
