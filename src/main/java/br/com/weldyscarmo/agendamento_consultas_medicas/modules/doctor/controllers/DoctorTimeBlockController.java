package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorTimeBlockRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorTimeBlockResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.BlockTimeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Bloqueio de intervalos")
public class DoctorTimeBlockController {

    @Autowired
    private BlockTimeUseCase blockTimeUseCase;

    @Operation(summary = "Bloquear um intervalo",
            description = "Essa função é responsável por bloquear um intervalo de tempo no expediente do médico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = DoctorTimeBlockResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Intervalo escolhido fora dos horários de atendimento de um médico",
                    content = {
                    @Content(schema = @Schema(implementation = DoctorTimeBlockResponseDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflito: A regra de horários foi violada", content = {
                    @Content(schema = @Schema(implementation = DoctorTimeBlockResponseDTO.class))
            })
    })
    @PostMapping("/block")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorTimeBlockResponseDTO> create
            (HttpServletRequest request, @RequestBody DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        DoctorTimeBlockResponseDTO result = this.blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
