package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorTimeBlockRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorTimeBlockResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases.BlockTimeUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/doctor")
public class DoctorTimeBlockController {

    @Autowired
    private BlockTimeUseCase blockTimeUseCase;

    @PostMapping("/block")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorTimeBlockResponseDTO> create
            (HttpServletRequest request, @RequestBody DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());
        DoctorTimeBlockResponseDTO result = this.blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
