package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.UpdateDataDoctorRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.CreateDoctorUseCase;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.UpdateDataDoctorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/doctor")
@Tag(name = "Informações do médico")
public class DoctorController {

    @Autowired
    private CreateDoctorUseCase createDoctorUseCase;

    @Autowired
    private UpdateDataDoctorUseCase updateDataDoctorUseCase;

    @Operation(summary = "Cadastrar médico",
            description = "Essa função é responsável pelo cadastramento de médicos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Usuário já existe", content = {
                    @Content(schema = @Schema(implementation = String.class))
            })
    })
    @PostMapping("/")
    public ResponseEntity<DoctorResponseDTO> create(@Valid @RequestBody CreateDoctorRequestDTO createDoctorRequestDTO){
        DoctorResponseDTO result = this.createDoctorUseCase.execute(createDoctorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Atualizar dados do médico",
            description = "Essa função é responsável pela atualização de dados do médico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
            })
    })
    @PatchMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponseDTO> update(@RequestBody UpdateDataDoctorRequestDTO updateDataDoctorRequestDTO,
                                                    HttpServletRequest request){
        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        DoctorResponseDTO result = this.updateDataDoctorUseCase.execute(doctorId, updateDataDoctorRequestDTO);
        return ResponseEntity.ok(result);
    }
}
