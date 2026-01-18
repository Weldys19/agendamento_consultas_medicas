package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidCredentialsException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.AuthPatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.AuthUser;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.JWTProvider;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthPatientUseCase {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTProvider jwtProvider;

    public TokenResponseDTO execute(AuthPatientRequestDTO authPatientRequestDTO){
        var patient = this.patientRepository.findByEmailIgnoreCase(authPatientRequestDTO.getEmail())
                .orElseThrow(() -> {
                   throw new InvalidCredentialsException();
                });

        var passwordMatchers = passwordEncoder.matches(authPatientRequestDTO.getPassword(),
                patient.getPassword());

        if (!passwordMatchers){
            throw new InvalidCredentialsException();
        }

        var authUser = AuthUser.builder()
                .id(patient.getId())
                .email(patient.getEmail())
                .password(patient.getPassword())
                .roles("PATIENT")
                .build();

        var token = this.jwtProvider.generateToken(authUser);

        return token;
    }
}
