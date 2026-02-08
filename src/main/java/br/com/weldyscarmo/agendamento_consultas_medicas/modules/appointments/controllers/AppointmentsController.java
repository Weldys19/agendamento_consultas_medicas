package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.AppointmentsResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.CreateAppointmentsRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentsController {

    @Autowired
    private CreateAppointmentsUseCase createAppointmentsUseCase;

    @Autowired
    private DoctorAppointmentsOnTheDayUseCase doctorAppointmentsOnTheDayUseCase;

    @Autowired
    private PatientAppointmentsUseCase patientAppointmentsUseCase;

    @Autowired
    private SetStatusToFinishedUseCase setStatusToFinishedUseCase;

    @Autowired
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @Tag(name = "Agendamento de consultas")
    @Operation(summary = "Agendar consultas",
            description = "Essa função é responsável por agendar novas consultas")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = AppointmentsResponseDTO.class))
            }),

            @ApiResponse(responseCode = "404", description = "Usuário não existe", content = {
                    @Content(schema = @Schema(implementation = String.class))
            }),

            @ApiResponse(responseCode = "400", description = "Médico não estará atendendo nesse dia/horário", content = {
                    @Content(schema = @Schema(implementation = String.class))
            }),

            @ApiResponse(responseCode = "409", description = "Conflito: A regra de horários foi violada", content = {
                    @Content(schema = @Schema(implementation = String.class))
            }),
    })
    @PostMapping("/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<AppointmentsResponseDTO> create(@RequestBody CreateAppointmentsRequestDTO createAppointmentsRequestDTO,
                                                          HttpServletRequest request, @PathVariable UUID doctorId){
        UUID patientId = UUID.fromString(request.getAttribute("user_id").toString());

        AppointmentsResponseDTO result = this.createAppointmentsUseCase.execute(patientId, doctorId,
                createAppointmentsRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Tag(name = "Informações do médico")
    @Operation(summary = "Listar consultas do médico",
            description = "Essa função é responsável por listar as consultas do médico no dia")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentsResponseDTO.class)))
            })
    })
    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<AppointmentsResponseDTO>> listAppointmentsDoctor(HttpServletRequest request){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        List<AppointmentsResponseDTO> result = this.doctorAppointmentsOnTheDayUseCase.execute(doctorId);
        return ResponseEntity.ok(result);
    }

    @Tag(name = "Informações do paciente")
    @Operation(summary = "Listar consultas do paciente",
            description = "Essa função é responsável por listar todas as consultas do paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentsResponseDTO.class)))
            })
    })
    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<List<AppointmentsResponseDTO>> listAppointmentsPatient(HttpServletRequest request){

        UUID patientId = UUID.fromString(request.getAttribute("user_id").toString());
        List<AppointmentsResponseDTO> result = this.patientAppointmentsUseCase.execute(patientId);
        return ResponseEntity.ok(result);
    }

    @Tag(name = "Agendamento de consultas")
    @Operation(summary = "Finalizar consulta",
            description = "Essa função é responsável por setar o status da consulta como finalizado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentsResponseDTO.class)))
            })
    })
    @PatchMapping("/{id}/finish")
    @PreAuthorize("hasRole('DOCTOR')")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<AppointmentsResponseDTO> setFinished(HttpServletRequest request, @PathVariable UUID id){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        AppointmentsResponseDTO result = this.setStatusToFinishedUseCase.execute(doctorId, id);
        return ResponseEntity.ok(result);
    }

    @Tag(name = "Agendamento de consultas")
    @Operation(summary = "Cancelar consulta",
            description = "Essa função é responsável por cancelar uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentsResponseDTO.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = {
                    @Content(schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "409", description = "O cancelamento só pode ser feito em até 2h antes da consulta",
                    content = {
                    @Content(schema = @Schema(implementation = String.class))
            })
    })
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<AppointmentsResponseDTO> setCanceled(HttpServletRequest request, @PathVariable UUID id){

        UUID userId = UUID.fromString(request.getAttribute("user_id").toString());

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();

        List<GrantedAuthority> roles = List.copyOf(authorities);
        String role = roles.getFirst().getAuthority();

        AppointmentsResponseDTO result = this.cancelAppointmentUseCase.execute(role, userId, id);
        return ResponseEntity.ok(result);
    }
}
