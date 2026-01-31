package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.PatientResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.UpdateDataPatientRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateDataPatientUseCase {

    @Autowired
    private PatientRepository patientRepository;

    @Transactional
    public PatientResponseDTO execute(UpdateDataPatientRequestDTO updateDataPatientRequestDTO,
                                      UUID idPatient){

        PatientEntity patientEntity = this.patientRepository.findById(idPatient).
                orElseThrow(() -> {
                    throw new UserNotFoundException();
                });

        if (updateDataPatientRequestDTO.getName() != null){
           patientEntity.setName(updateDataPatientRequestDTO.getName());
        }
        if (updateDataPatientRequestDTO.getUsername() != null){
            patientEntity.setUsername(updateDataPatientRequestDTO.getUsername());
        }

        return PatientResponseDTO.builder()
                .id(patientEntity.getId())
                .name(patientEntity.getName())
                .username(patientEntity.getUsername())
                .email(patientEntity.getEmail())
                .createdAt(patientEntity.getCreatedAt())
                .build();
    }
}
