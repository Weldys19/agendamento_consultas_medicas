package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.PatientResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.UpdateDataPatientRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateDataPatientUseCaseTest {

    @InjectMocks
    private UpdateDataPatientUseCase updateDataPatientUseCase;

    @Mock
    private PatientRepository patientRepository;

    @Test
    public void shouldUpdatePatientNameAndUsername(){

        UpdateDataPatientRequestDTO updatePatient = UpdateDataPatientRequestDTO.builder()
                .name("weldys do carmo")
                .username("weldys002")
                .build();

        PatientEntity patientEntity = PatientEntity.builder()
                .id(UUID.randomUUID())
                .name("weldys")
                .username("weldyscarmo")
                .build();

        when(this.patientRepository.findById(patientEntity.getId()))
                .thenReturn(Optional.of(patientEntity));

        PatientResponseDTO result = this.updateDataPatientUseCase.execute(updatePatient,
                patientEntity.getId());

        assertThat(result.getName()).isEqualTo(updatePatient.getName());
        assertThat(result.getUsername()).isEqualTo(updatePatient.getUsername());
    }

    @Test
    public void shouldUpdatePatientName(){

        UpdateDataPatientRequestDTO updatePatient = UpdateDataPatientRequestDTO.builder()
                .name("weldys do carmo")
                .build();

        PatientEntity patientEntity = PatientEntity.builder()
                .id(UUID.randomUUID())
                .name("weldys")
                .username("weldyscarmo")
                .build();

        when(this.patientRepository.findById(patientEntity.getId()))
                .thenReturn(Optional.of(patientEntity));

        PatientResponseDTO result = this.updateDataPatientUseCase.execute(updatePatient,
                patientEntity.getId());

        assertThat(result.getName()).isEqualTo(updatePatient.getName());
        assertThat(result.getUsername()).isEqualTo(patientEntity.getUsername());
    }

    @Test
    public void shouldUpdatePatientUsername(){

        UpdateDataPatientRequestDTO updatePatient = UpdateDataPatientRequestDTO.builder()
                .username("weldys002")
                .build();

        PatientEntity patientEntity = PatientEntity.builder()
                .id(UUID.randomUUID())
                .name("weldys")
                .username("weldyscarmo")
                .build();

        when(this.patientRepository.findById(patientEntity.getId()))
                .thenReturn(Optional.of(patientEntity));

        PatientResponseDTO result = this.updateDataPatientUseCase.execute(updatePatient,
                patientEntity.getId());

        assertThat(result.getName()).isEqualTo(patientEntity.getName());
        assertThat(result.getUsername()).isEqualTo(updatePatient.getUsername());
    }

    @Test
    public void shouldNotUpdatePatient(){

        when(this.patientRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            this.updateDataPatientUseCase.execute(new UpdateDataPatientRequestDTO(), UUID.randomUUID());
        }).isInstanceOf(UserNotFoundException.class);
    }
}
