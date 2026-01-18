package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.AuthPatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases.AuthPatientUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
public class AuthPatientController {

    @Autowired
    private AuthPatientUseCase authPatientUseCase;

    @PostMapping("/auth")
    public ResponseEntity<Object> create(@RequestBody AuthPatientRequestDTO authPatientRequestDTO){
        var result = this.authPatientUseCase.execute(authPatientRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
