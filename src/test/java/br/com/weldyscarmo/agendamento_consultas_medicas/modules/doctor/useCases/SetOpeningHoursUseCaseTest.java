package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidScheduleException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.OverlappingSchedulesException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorScheduleRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorScheduleResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SetOpeningHoursUseCaseTest {

    @InjectMocks
    private SetOpeningHoursUseCase setOpeningHoursUseCase;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    private UUID doctorId;

    @BeforeEach
    void setup() {
        doctorId = UUID.randomUUID();
    }

    @Nested
    class WhenScheduleIsValid {

        @Test
        public void shouldCreateDoctorSchedule(){

            CreateDoctorScheduleRequestDTO dto = builderCreateDoctorSchedule(
                    DayOfWeek.MONDAY, LocalTime.of(9,0), LocalTime.of(18, 0));

            DoctorScheduleEntity doctorScheduleEntity = builderDoctorScheduleEntity(doctorId,
                    dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());

            when(doctorScheduleRepository.findAllByDoctorId(doctorId))
                    .thenReturn(List.of());

            when(doctorScheduleRepository.save(any(DoctorScheduleEntity.class)))
                    .thenReturn(doctorScheduleEntity);

            DoctorScheduleResponseDTO result = setOpeningHoursUseCase.execute(doctorId, dto);

            assertThat(result.getId()).isNotNull();
            assertThat(result.getDoctorId()).isEqualTo(doctorId);
            assertThat(result.getDayOfWeek()).isEqualTo(dto.getDayOfWeek());
            assertThat(result.getStartTime()).isEqualTo(dto.getStartTime());
            assertThat(result.getEndTime()).isEqualTo(dto.getEndTime());
        }
    }

    @Nested
    class WhenScheduleIsInvalid{

        CreateDoctorScheduleRequestDTO dto;

        @BeforeEach
        void setup(){
            dto = builderCreateDoctorSchedule(
                    DayOfWeek.MONDAY, LocalTime.of(9,0), LocalTime.of(18, 0));
        }

        @Test
        public void shouldThrowExceptionWhenStartTimeIsAfterEndTime() {

             dto = builderCreateDoctorSchedule(
                    DayOfWeek.MONDAY, LocalTime.of(15,0), LocalTime.of(9, 0));

            assertThatThrownBy(() -> {
                setOpeningHoursUseCase.execute(doctorId, dto);
            }).isInstanceOf(InvalidScheduleException.class);
        }

        @Test
        public void shouldThrowExceptionWhenStartTimeConflictsWithEndTimeOnSameDay(){

            DoctorScheduleEntity doctorScheduleEntity = builderDoctorScheduleEntity(doctorId,
                    dto.getDayOfWeek(), LocalTime.of(16,0), LocalTime.of(20, 0));

            when(doctorScheduleRepository.findAllByDoctorId(doctorId))
                    .thenReturn(List.of(doctorScheduleEntity));

            assertThatThrownBy(() -> {
                setOpeningHoursUseCase.execute(doctorId, dto);
            }).isInstanceOf(OverlappingSchedulesException.class);
        }

        @Test
        public void shouldThrowExceptionWhenEndTimeConflictsWithStartTimeOnSameDay(){

            DoctorScheduleEntity doctorScheduleEntity = builderDoctorScheduleEntity(doctorId,
                    dto.getDayOfWeek(), LocalTime.of(5,0), LocalTime.of(10, 0));

            when(doctorScheduleRepository.findAllByDoctorId(doctorId))
                    .thenReturn(List.of(doctorScheduleEntity));

            assertThatThrownBy(() -> {
                setOpeningHoursUseCase.execute(doctorId, dto);
            }).isInstanceOf(OverlappingSchedulesException.class);
        }
    }

    private CreateDoctorScheduleRequestDTO builderCreateDoctorSchedule(DayOfWeek day, LocalTime start, LocalTime end){
        return CreateDoctorScheduleRequestDTO.builder()
                .dayOfWeek(day)
                .startTime(start)
                .endTime(end)
                .build();
    }

    private DoctorScheduleEntity builderDoctorScheduleEntity(UUID doctorId, DayOfWeek day, LocalTime start, LocalTime end) {

        return DoctorScheduleEntity.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .dayOfWeek(day)
                .startTime(start)
                .endTime(end)
                .build();
    }
}