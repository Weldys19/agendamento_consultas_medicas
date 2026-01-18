package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.CreatePatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.CreatePatientResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreatePatientUseCase {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CreatePatientResponseDTO execute(CreatePatientRequestDTO createPatientRequestDTO){

        this.patientRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(createPatientRequestDTO.getUsername(),
                createPatientRequestDTO.getEmail()).ifPresent(user -> {
                    throw new UserFoundException();
        });

        var hashPassword = passwordEncoder.encode(createPatientRequestDTO.getPassword());

        var patientEntity = PatientEntity.builder()
                .name(createPatientRequestDTO.getName())
                .email(createPatientRequestDTO.getEmail())
                .username(createPatientRequestDTO.getUsername())
                .password(hashPassword)
                .build();

        var savedPatient = this.patientRepository.save(patientEntity);

        return CreatePatientResponseDTO.builder()
                .id(savedPatient.getId())
                .name(savedPatient.getName())
                .username(savedPatient.getUsername())
                .email(savedPatient.getEmail())
                .createdAt(savedPatient.getCreatedAt())
                .build();
    }
}
