package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.CreatePatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.PatientResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.UpdateDataPatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases.CreatePatientUseCase;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases.UpdateDataPatientUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patient")
@Tag(name = "Informações do paciente")
public class PatientController {

    @Autowired
    private CreatePatientUseCase createPatientUseCase;

    @Autowired
    private UpdateDataPatientUseCase updateDataPatientUseCase;

    @Operation(summary = "Cadastrar paciente",
            description = "Essa função é responsável pelo cadastramento de novos pacientes")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = PatientResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Usuário já existe")
    })
    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody CreatePatientRequestDTO createPatientRequestDTO){
        PatientResponseDTO result = this.createPatientUseCase.execute(createPatientRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Atualizar dados do paciente",
            description = "Essa função é responsável pela atualização de dados do paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = PatientResponseDTO.class))
            })
    })
    @PatchMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<PatientResponseDTO> update(HttpServletRequest request,
            @Valid @RequestBody UpdateDataPatientRequestDTO updateDataPatientRequestDTO){

        PatientResponseDTO result = this.updateDataPatientUseCase.execute(updateDataPatientRequestDTO, request);
        return ResponseEntity.ok(result);
    }
}
