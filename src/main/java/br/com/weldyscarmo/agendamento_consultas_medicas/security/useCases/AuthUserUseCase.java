package br.com.weldyscarmo.agendamento_consultas_medicas.security.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidCredentialsException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.JWTProvider;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.AuthRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.AuthUserDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserUseCase {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTProvider jwtProvider;

    public TokenResponseDTO execute(AuthRequestDTO authRequestDTO){

        var authUserDTO = mapUser(authRequestDTO);

        var passwordMatchers = passwordEncoder.matches(authRequestDTO.getPassword(),
                authUserDTO.getPassword());

        if (!passwordMatchers){
            throw new InvalidCredentialsException();
        }

        var token = this.jwtProvider.generateToken(authUserDTO);

        return token;
    }

    private AuthUserDTO mapUser(AuthRequestDTO authRequestDTO){

        var authUserDTO = AuthUserDTO.builder()
                .email(authRequestDTO.getEmail())
                .build();

        this.patientRepository.findByEmailIgnoreCase(authRequestDTO.getEmail())
                .ifPresent(patientEntity -> {
                    authUserDTO.setId(patientEntity.getId());
                    authUserDTO.setPassword(patientEntity.getPassword());
                    authUserDTO.setRoles("PATIENT");
                });

        this.doctorRepository.findByEmailIgnoreCase(authRequestDTO.getEmail())
                .ifPresent(doctorEntity -> {
                    authUserDTO.setId(doctorEntity.getId());
                    authUserDTO.setPassword(doctorEntity.getPassword());
                    authUserDTO.setRoles("DOCTOR");
                });

        if (authUserDTO.getId() == null){
            throw new InvalidCredentialsException();
        }

        return authUserDTO;
    }
}
