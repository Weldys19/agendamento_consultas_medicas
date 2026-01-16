package br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.dtos.CreatePatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreatePatientUseCase {

    @Autowired
    private PatientRepository patientRepository;

    public PatientEntity execute(CreatePatientDTO createPatientDTO){

        var patientEntity = PatientEntity.builder()
                .name(createPatientDTO.getName())
                .email(createPatientDTO.getEmail())
                .username(createPatientDTO.getUsername())
                .password(createPatientDTO.getPassword())
                .build();

        this.patientRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(patientEntity.getUsername(),
                patientEntity.getEmail()).ifPresent(user -> {
                    throw new UserFoundException();
        });

        return this.patientRepository.save(patientEntity);
    }
}
