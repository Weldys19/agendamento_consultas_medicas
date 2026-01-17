package br.com.weldyscarmo.agendamento_consultas_medicas.user.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.dtos.CreatePatientDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.user.useCases.CreatePatientUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatePatientUseCaseTest {

    @InjectMocks
    private CreatePatientUseCase createPatientUseCase;

    @Mock
    private PatientRepository patientRepository;

    @Test
    public void itShouldBePossibleToCreateAPatient(){

        var patient = CreatePatientDTO.builder()
                .name("Weldys")
                .email("weldyscarmo@gmail.com")
                .username("WeldysdoCarmo")
                .password("12345678910")
                .build();

        var patientIdGenerate = PatientEntity.builder()
                .id(UUID.randomUUID())
                .build();

        when(this.patientRepository.save(any(PatientEntity.class))).thenReturn(patientIdGenerate);

        var result = this.createPatientUseCase.execute(patient);

        assertThat(result).hasFieldOrProperty("id");
        assertNotNull(result.getId());
    }

    @Test
    public void itShouldNotBePossibleToCreateAPatient(){

        var patient = CreatePatientDTO.builder()
                .email("weldyscarmo@gmail.com")
                .username("WeldysdoCarmo")
                .build();

        var patientEntity = PatientEntity.builder()
                .email("weldyscarmo@gmail.com")
                .username("WeldysdoCarmo")
                .build();

        when(this.patientRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(patient.getUsername(),
                patient.getEmail())).thenReturn(Optional.of(patientEntity));

        assertThrows(UserFoundException.class, () -> {
           createPatientUseCase.execute(patient);
        });
    }

}
