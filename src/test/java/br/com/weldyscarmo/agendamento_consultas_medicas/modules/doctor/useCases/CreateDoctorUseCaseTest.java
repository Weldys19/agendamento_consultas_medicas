package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorRequestDTO;
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
public class CreateDoctorUseCaseTest {

    @InjectMocks
    private CreateDoctorUseCase createDoctorUseCase;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void itShouldBePossibleToCreateADoctor(){

        var doctorDTO = CreateDoctorRequestDTO.builder()
                .email("weldys@gmail.com")
                .password("1234567890")
                .build();

        var doctorIdGenerated = DoctorEntity.builder()
                .id(UUID.randomUUID())
                .build();

        when(this.doctorRepository.findByEmailIgnoreCase(doctorDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(this.passwordEncoder.encode(doctorDTO.getPassword()))
                .thenReturn("hashPassword");

        when(this.doctorRepository.save(any(DoctorEntity.class))).thenReturn(doctorIdGenerated);

        var result = this.createDoctorUseCase.execute(doctorDTO);

        assertThat(result.getId()).isEqualTo(doctorIdGenerated.getId());
        verify(passwordEncoder).encode(any(String.class));
    }

    @Test
    public void itShouldNotBePossibleToCreateADoctor(){

        var doctorDTO = CreateDoctorRequestDTO.builder()
                .email("weldys@gmail.com")
                .password("1234567890")
                .build();

        var doctorEntity = DoctorEntity.builder()
                .email("weldys@gmail.com")
                .password("1234567890")
                .build();

        when(this.doctorRepository.findByEmailIgnoreCase(doctorDTO.getEmail()))
                .thenReturn(Optional.of(doctorEntity));

        assertThrows(UserFoundException.class, () -> {
            this.createDoctorUseCase.execute(doctorDTO);
        });
    }
}
