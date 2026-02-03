package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.TimeNotFoundException;
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

    UUID doctorId;

    @BeforeEach
    void setup(){
        doctorId = UUID.randomUUID();
    }

    @Nested
    class WhenDoctorScheduleExists{

        DoctorScheduleEntity doctorScheduleEntity;

        @BeforeEach
        void setup(){
            doctorScheduleEntity = builderDoctorSchedule(doctorId);

            when(doctorScheduleRepository.findByIdAndDoctorId(doctorScheduleEntity.getId(), doctorId))
                    .thenReturn(Optional.of(doctorScheduleEntity));
        }

        @Test
        public void shouldDeleteScheduleWhenItExists(){

            deleteOpeningHoursUseCase.execute(doctorId, doctorScheduleEntity.getId());

            verify(doctorScheduleRepository).delete(doctorScheduleEntity);
        }
    }

    @Nested
    class WhenDoctorScheduleNotExists{

        @Test
        public void shouldNotDeleteScheduleWhenItDoesNotExist(){

            when(doctorScheduleRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                deleteOpeningHoursUseCase.execute(doctorId, UUID.randomUUID());
            }).isInstanceOf(TimeNotFoundException.class);
        }
    }

    private DoctorScheduleEntity builderDoctorSchedule(UUID doctorId){
        return DoctorScheduleEntity.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .build();
    }
}
