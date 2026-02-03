package br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.CreatePatientRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.dtos.PatientResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatePatientUseCaseTest {

    @InjectMocks
    private CreatePatientUseCase createPatientUseCase;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DoctorRepository doctorRepository;

    @Test
    public void itShouldBePossibleToCreateAPatient(){

        CreatePatientRequestDTO patient = CreatePatientRequestDTO.builder()
                .name("Weldys")
                .email("weldyscarmo@gmail.com")
                .username("WeldysdoCarmo")
                .password("12345678910")
                .build();

        PatientEntity patientIdGenerate = PatientEntity.builder()
                .id(UUID.randomUUID())
                .build();


        when(this.patientRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(patient.getUsername(),
                patient.getEmail())).thenReturn(Optional.empty());

        when(this.doctorRepository.findByEmailIgnoreCase(patient.getEmail()))
                .thenReturn(Optional.empty());

        when(this.patientRepository.save(any(PatientEntity.class))).thenReturn(patientIdGenerate);

        when(this.passwordEncoder.encode(patient.getPassword())).thenReturn("hashPassword");

        PatientResponseDTO result = this.createPatientUseCase.execute(patient);

        assertThat(result.getId()).isEqualTo(patientIdGenerate.getId());
        verify(passwordEncoder).encode(any(String.class));
    }

    @Test
    public void itShouldNotBePossibleToCreateAPatient(){

        CreatePatientRequestDTO patient = CreatePatientRequestDTO.builder()
                .email("weldyscarmo@gmail.com")
                .username("WeldysdoCarmo")
                .build();

        PatientEntity patientEntity = PatientEntity.builder()
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
