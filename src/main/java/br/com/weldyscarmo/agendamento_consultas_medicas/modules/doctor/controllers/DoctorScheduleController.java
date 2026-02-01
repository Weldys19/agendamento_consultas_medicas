package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.ErrorMessageDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorScheduleRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorScheduleResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.DeleteOpeningHoursUseCase;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.SetOpeningHoursUseCase;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/doctor")
@Tag(name = "Disponibilidade do Médico")
public class DoctorScheduleController {

    @Autowired
    private SetOpeningHoursUseCase setOpeningHoursUseCase;

    @Autowired
    private DeleteOpeningHoursUseCase deleteOpeningHoursUseCase;

    @Operation(summary = "Definir horários de atendimento",
            description = "Essa função é responsável por definir os horários de atendimento de um médico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = DoctorScheduleResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400",
                    description = "O horário inical não pode ser depois do que o horário final", content = {
                    @Content(schema = @Schema(implementation = ErrorMessageDTO.class))
            }),
            @ApiResponse(responseCode = "409",
                    description = "Não pode ter sobreposição de horários", content = {
                    @Content(schema = @Schema(implementation = ErrorMessageDTO.class))
            })
    })
    @PostMapping("/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleResponseDTO> create(HttpServletRequest request,
                        @RequestBody CreateDoctorScheduleRequestDTO createDoctorScheduleRequestDTO){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());

        DoctorScheduleResponseDTO result = this.setOpeningHoursUseCase.execute(doctorId, createDoctorScheduleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable UUID id){
        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        this.deleteOpeningHoursUseCase.execute(doctorId, id);
        return ResponseEntity.noContent().build();
    }
}
