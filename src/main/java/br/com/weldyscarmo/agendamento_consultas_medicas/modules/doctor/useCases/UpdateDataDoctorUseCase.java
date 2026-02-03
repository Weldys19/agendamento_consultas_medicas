package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.UpdateDataDoctorRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateDataDoctorUseCase {

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponseDTO execute(UUID doctorId, UpdateDataDoctorRequestDTO updateDataDoctorRequestDTO){

        DoctorEntity doctorEntity = this.doctorRepository.findById(doctorId).orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        if (updateDataDoctorRequestDTO.getName() != null){
            doctorEntity.setName(updateDataDoctorRequestDTO.getName());
        }
        if (updateDataDoctorRequestDTO.getSpeciality() != null){
            doctorEntity.setSpecialty(updateDataDoctorRequestDTO.getSpeciality());
        }
        if (updateDataDoctorRequestDTO.getConsultationDurationInMinutes() != null){
            doctorEntity.setConsultationDurationInMinutes
                    (updateDataDoctorRequestDTO.getConsultationDurationInMinutes());
        }

        return DoctorResponseDTO.builder()
                .id(doctorEntity.getId())
                .name(doctorEntity.getName())
                .email(doctorEntity.getEmail())
                .specialty(doctorEntity.getSpecialty())
                .consultationDurationInMinutes(doctorEntity.getConsultationDurationInMinutes())
                .createdAt(doctorEntity.getCreatedAt())
                .build();
    }
}
