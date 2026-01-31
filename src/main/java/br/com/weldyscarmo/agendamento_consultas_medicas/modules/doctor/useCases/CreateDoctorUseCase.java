package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateDoctorUseCase {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DoctorResponseDTO execute(CreateDoctorRequestDTO createDoctorRequestDTO){
        this.doctorRepository.findByEmailIgnoreCase(createDoctorRequestDTO.getEmail()).ifPresent(user -> {
                    throw new UserFoundException();
        });

        this.patientRepository.findByEmailIgnoreCase(createDoctorRequestDTO.getEmail()).ifPresent(user -> {
            throw new UserFoundException();
        });

        String hashPassword = passwordEncoder.encode(createDoctorRequestDTO.getPassword());

        DoctorEntity doctorEntity = DoctorEntity.builder()
                .name(createDoctorRequestDTO.getName())
                .email(createDoctorRequestDTO.getEmail())
                .specialty(createDoctorRequestDTO.getSpecialty())
                .consultationDurationInMinutes(createDoctorRequestDTO.getConsultationDurationInMinutes())
                .password(hashPassword)
                .build();

        DoctorEntity savedDoctor = this.doctorRepository.save(doctorEntity);

        return DoctorResponseDTO.builder()
                .id(savedDoctor.getId())
                .name(savedDoctor.getName())
                .email(savedDoctor.getEmail())
                .specialty(savedDoctor.getSpecialty())
                .consultationDurationInMinutes(savedDoctor.getConsultationDurationInMinutes())
                .createdAt(savedDoctor.getCreatedAt())
                .build();
    }
}
