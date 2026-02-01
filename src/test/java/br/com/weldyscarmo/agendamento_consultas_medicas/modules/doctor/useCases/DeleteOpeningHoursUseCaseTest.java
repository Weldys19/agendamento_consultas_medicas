package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidTimeExclusionException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.TimeNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteOpeningHoursUseCaseTest {

    @InjectMocks
    private DeleteOpeningHoursUseCase deleteOpeningHoursUseCase;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Nested
    class WhenDoctorExists{

        DoctorEntity doctorEntity;

        @BeforeEach
        void setup(){
            doctorEntity = builderDoctorEntity();

            when(doctorRepository.findById(doctorEntity.getId()))
                    .thenReturn(Optional.of(doctorEntity));
        }

        @Nested
        class WhenDoctorScheduleExists{

            DoctorScheduleEntity doctorScheduleEntity;

            @BeforeEach
            void setup(){
                doctorScheduleEntity = builderDoctorSchedule(doctorEntity);

                when(doctorScheduleRepository.findById(doctorScheduleEntity.getId()))
                        .thenReturn(Optional.of(doctorScheduleEntity));
            }

            @Test
            public void shouldDeleteScheduleWhenItExists(){

                deleteOpeningHoursUseCase.execute(doctorEntity.getId(), doctorScheduleEntity.getId());

                verify(doctorScheduleRepository).delete(doctorScheduleEntity);
            }

            @Test
            public void shouldNotDeleteScheduleWhenItBelongsToAnotherDoctor(){

                doctorScheduleEntity.setDoctorId(UUID.randomUUID());

                assertThatThrownBy(() -> {
                    deleteOpeningHoursUseCase.execute(doctorEntity.getId(), doctorScheduleEntity.getId());
                }).isInstanceOf(InvalidTimeExclusionException.class);
            }
        }

        @Nested
        class WhenDoctorScheduleNotExists{

            @Test
            public void shouldNotDeleteScheduleWhenItDoesNotExist(){

                when(doctorScheduleRepository.findById(any(UUID.class)))
                        .thenReturn(Optional.empty());

                assertThatThrownBy(() -> {
                    deleteOpeningHoursUseCase.execute(doctorEntity.getId(), UUID.randomUUID());
                }).isInstanceOf(TimeNotFoundException.class);
            }
        }
    }

    @Nested
    class WhenDoctorNotExists{

        @Test
        public void shouldNotDeleteDoctorWhenItDoesNotExist(){

            when(doctorRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                deleteOpeningHoursUseCase.execute(UUID.randomUUID(), UUID.randomUUID());
            }).isInstanceOf(UserNotFoundException.class);
        }
    }

    private DoctorEntity builderDoctorEntity(){
        return DoctorEntity.builder()
                .id(UUID.randomUUID())
                .build();
    }

    private DoctorScheduleEntity builderDoctorSchedule(DoctorEntity doctorEntity){
        return DoctorScheduleEntity.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorEntity.getId())
                .build();
    }
}
