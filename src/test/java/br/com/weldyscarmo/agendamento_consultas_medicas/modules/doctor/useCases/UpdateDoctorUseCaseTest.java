package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.UpdateDataDoctorRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
public class UpdateDoctorUseCaseTest {

    @InjectMocks
    private UpdateDataDoctorUseCase updateDataDoctorUseCase;

    @Mock
    private DoctorRepository doctorRepository;

    @Nested
    class WhenDoctorExists{

        DoctorEntity doctorEntity;
        UpdateDataDoctorRequestDTO requestDTO;

        @BeforeEach
        void setup(){
            doctorEntity = builderDoctorEntity();
            requestDTO = builderRequestDTO();

            when(doctorRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(doctorEntity));
        }

        @Test
        public void shouldUpdateAllDoctorFieldsWhenAllDataIsProvided(){

            DoctorResponseDTO result = updateDataDoctorUseCase.execute(doctorEntity.getId(), requestDTO);

            assertThat(result.getName()).isEqualTo(requestDTO.getName());
            assertThat(result.getSpecialty()).isEqualTo(requestDTO.getSpeciality());
            assertThat(result.getConsultationDurationInMinutes())
                    .isEqualTo(requestDTO.getConsultationDurationInMinutes());
        }

        @Test
        public void shouldUpdateOnlyNameWhenOnlyNameIsProvided(){

            requestDTO.setSpeciality(null);
            requestDTO.setConsultationDurationInMinutes(null);

            DoctorResponseDTO result = updateDataDoctorUseCase.execute(doctorEntity.getId(), requestDTO);

            assertThat(result.getName()).isEqualTo(requestDTO.getName());
            assertThat(result.getSpecialty()).isEqualTo(doctorEntity.getSpecialty());
            assertThat(result.getConsultationDurationInMinutes())
                    .isEqualTo(doctorEntity.getConsultationDurationInMinutes());
        }
    }

    @Nested
    class WhenDoctorNotExists{

        @Test
        public void shouldNotUpdateDoctorWhenDoctorDoesNotExist(){

            when(doctorRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                updateDataDoctorUseCase.execute(UUID.randomUUID(), new UpdateDataDoctorRequestDTO());
            }).isInstanceOf(UserNotFoundException.class);
        }
    }

    private DoctorEntity builderDoctorEntity(){
        return DoctorEntity.builder()
                .id(UUID.randomUUID())
                .name("weldys")
                .specialty("Cirurgião")
                .consultationDurationInMinutes(30L)
                .build();
    }

    private UpdateDataDoctorRequestDTO builderRequestDTO(){
        return UpdateDataDoctorRequestDTO.builder()
                .name("gabriel")
                .speciality("Ginecologista")
                .consultationDurationInMinutes(60L)
                .build();
    }
}
