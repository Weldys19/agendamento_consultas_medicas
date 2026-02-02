package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.CreateAppointmentsRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.AppointmentsResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases.CreateAppointmentsUseCase;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases.DoctorAppointmentsOnTheDayUseCase;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases.PatientAppointmentsUseCase;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Agendamento de consultas")
public class AppointmentsController {

    @Autowired
    private CreateAppointmentsUseCase createAppointmentsUseCase;

    @Autowired
    private DoctorAppointmentsOnTheDayUseCase doctorAppointmentsOnTheDayUseCase;

    @Autowired
    private PatientAppointmentsUseCase patientAppointmentsUseCase;

    @Operation(summary = "Agendar consultas",
            description = "Essa função é responsável por agendar novas consultas")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = AppointmentsResponseDTO.class))
            }),

            @ApiResponse(responseCode = "404", description = "Usuário não existe", content = {
                    @Content(schema = @Schema(implementation = String.class))
            }),

            @ApiResponse(responseCode = "409", description = "Conflito: A regra de horários foi violada", content = {
                    @Content(schema = @Schema(implementation = String.class))
            }),
    })
    @PostMapping("/patient/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentsResponseDTO> create(@RequestBody CreateAppointmentsRequestDTO createAppointmentsRequestDTO,
                                                          HttpServletRequest request, @PathVariable UUID doctorId){
        UUID patientId = UUID.fromString(request.getAttribute("user_id").toString());

        AppointmentsResponseDTO result = this.createAppointmentsUseCase.execute(patientId, doctorId,
                createAppointmentsRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentsResponseDTO>> listAppointmentsDoctor(HttpServletRequest request){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        List<AppointmentsResponseDTO> result = this.doctorAppointmentsOnTheDayUseCase.execute(doctorId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentsResponseDTO>> listAppointmentsPatient(HttpServletRequest request){

        UUID patientId = UUID.fromString(request.getAttribute("user_id").toString());
        List<AppointmentsResponseDTO> result = this.patientAppointmentsUseCase.execute(patientId);
        return ResponseEntity.ok(result);
    }
}
