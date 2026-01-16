package br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.dtos.CreatePatientDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.useCases.CreatePatientUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private CreatePatientUseCase createPatientUseCase;

    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody CreatePatientDTO createPatientDTO){
        var result = this.createPatientUseCase.execute(createPatientDTO);
        return ResponseEntity.ok().body(result);
    }
}
