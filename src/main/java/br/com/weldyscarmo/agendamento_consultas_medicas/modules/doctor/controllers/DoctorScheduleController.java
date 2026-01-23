package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorScheduleRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorScheduleResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.SetOpeningHoursUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
public class DoctorScheduleController {

    @Autowired
    private SetOpeningHoursUseCase setOpeningHoursUseCase;

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleResponseDTO> create(HttpServletRequest request,
                        @RequestBody CreateDoctorScheduleRequestDTO createDoctorScheduleRequestDTO){

        DoctorScheduleResponseDTO result = this.setOpeningHoursUseCase.execute(request, createDoctorScheduleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
