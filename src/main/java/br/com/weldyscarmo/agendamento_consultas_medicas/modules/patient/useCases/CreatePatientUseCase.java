package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.CreatePatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.PatientResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.utils.MapperPatientResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreatePatientUseCase {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PatientResponseDTO execute(CreatePatientRequestDTO createPatientRequestDTO){

        this.patientRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(createPatientRequestDTO.getUsername(),
            createPatientRequestDTO.getEmail()).ifPresent(user -> {
                throw new UserFoundException();
        });

        this.doctorRepository.findByEmailIgnoreCase(createPatientRequestDTO.getEmail()).ifPresent(user -> {
            throw new UserFoundException();
        });

        String hashPassword = passwordEncoder.encode(createPatientRequestDTO.getPassword());

        PatientEntity patientEntity = PatientEntity.builder()
                .name(createPatientRequestDTO.getName())
                .email(createPatientRequestDTO.getEmail())
                .username(createPatientRequestDTO.getUsername())
                .password(hashPassword)
                .build();

        PatientEntity savedPatient = this.patientRepository.save(patientEntity);

        return MapperPatientResponseDTO.mapperPatientResponse(savedPatient);
    }
}
