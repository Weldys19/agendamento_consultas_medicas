package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.*;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorTimeBlockEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorTimeBlockRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorTimeBlockRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorTimeBlockResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlockTimeUseCaseTest {

    @InjectMocks
    private BlockTimeUseCase blockTimeUseCase;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private AppointmentsRepository appointmentsRepository;

    @Mock
    private DoctorTimeBlockRepository doctorTimeBlockRepository;

    UUID doctorId;
    DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO;
    List<DoctorScheduleEntity> schedules;
    List<AppointmentsEntity> appointments;
    List<DoctorTimeBlockEntity> timesBlock;

    @BeforeEach
    void setup(){
        doctorId = UUID.randomUUID();
        doctorTimeBlockRequestDTO = builderTimeBlockRequestDTO();
        schedules = builderSchedules();
        appointments = builderAppointments();
        timesBlock = builderTimeBlock();
    }

    @Nested
    class WhenHoursIsValid{

        @Test
        public void shouldBePossibleToBlockTimeSlot(){

            DoctorTimeBlockEntity doctorTimeBlockEntity = builderDoctorTimeBlockEntity(doctorTimeBlockRequestDTO);

            when(doctorScheduleRepository
                    .findAllByDoctorIdAndDayOfWeek(doctorId, doctorTimeBlockRequestDTO.getDate().getDayOfWeek()))
                    .thenReturn(schedules);

            when(appointmentsRepository.findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate()))
                    .thenReturn(appointments);

            when(doctorTimeBlockRepository.findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate()))
                    .thenReturn(timesBlock);

            when(doctorTimeBlockRepository.save(any(DoctorTimeBlockEntity.class)))
                    .thenReturn(doctorTimeBlockEntity);

            DoctorTimeBlockResponseDTO result = blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);

            verify(doctorTimeBlockRepository).save(any(DoctorTimeBlockEntity.class));
            assertThat(result.getId()).isNotNull();
            assertThat(result.getDoctorId()).isEqualTo(doctorId);
            assertThat(result.getDate()).isEqualTo(doctorTimeBlockRequestDTO.getDate());
            assertThat(result.getStartTime()).isEqualTo(doctorTimeBlockRequestDTO.getStartTime());
            assertThat(result.getEndTime()).isEqualTo(doctorTimeBlockRequestDTO.getEndTime());
        }
    }

    @Nested
    class WhenHoursIsInvalid{

        @Test
        public void shouldNotAllowBlockingTimeInPast(){

            doctorTimeBlockRequestDTO.setDate(LocalDate.now().minusDays(2));

            assertThatThrownBy(() -> {
                blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
            }).isInstanceOf(InvalidDateException.class);
        }

        @Test
        public void shouldNotAllowBlockingTimeWhenStartTimeIsAfterEndTime(){

            doctorTimeBlockRequestDTO.setStartTime(LocalTime.of(18, 0));

            assertThatThrownBy(() -> {
                blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
            }).isInstanceOf(InvalidScheduleException.class);
        }

        @Test
        public void shouldNotBlockTimeWhenOutsideWorkingHours() {

            doctorTimeBlockRequestDTO.setStartTime(LocalTime.of(6, 0));

            when(doctorScheduleRepository
                    .findAllByDoctorIdAndDayOfWeek(doctorId, doctorTimeBlockRequestDTO.getDate().getDayOfWeek()))
                    .thenReturn(schedules);

            assertThatThrownBy(() -> {
                blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
            }).isInstanceOf(TimeSlotUnavailableForBlockingException.class);
        }

        @Test
        public void shouldNotBlockTimeWhenItConflictsWithScheduledAppointments(){

            doctorTimeBlockRequestDTO.setStartTime(LocalTime.of(10, 0));

            when(doctorScheduleRepository
                    .findAllByDoctorIdAndDayOfWeek(doctorId, doctorTimeBlockRequestDTO.getDate().getDayOfWeek()))
                    .thenReturn(schedules);

            when(appointmentsRepository.findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate()))
                    .thenReturn(appointments);

            assertThatThrownBy(() -> {
                blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
            }).isInstanceOf(ConflictWithSchedulesException.class);
        }

        @Test
        public void shouldNotBlockTimeWhenItConflictsWithAnAlreadyBlockedTime(){

            doctorTimeBlockRequestDTO.setStartTime(LocalTime.of(11, 0));

            when(doctorScheduleRepository
                    .findAllByDoctorIdAndDayOfWeek(doctorId, doctorTimeBlockRequestDTO.getDate().getDayOfWeek()))
                    .thenReturn(schedules);

            when(appointmentsRepository.findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate()))
                    .thenReturn(appointments);

            when(doctorTimeBlockRepository.findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate()))
                    .thenReturn(timesBlock);

            assertThatThrownBy(() -> {
                blockTimeUseCase.execute(doctorId, doctorTimeBlockRequestDTO);
            }).isInstanceOf(OverlappingSchedulesException.class);
        }
    }

    private DoctorTimeBlockRequestDTO builderTimeBlockRequestDTO(){
        return DoctorTimeBlockRequestDTO.builder()
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
    }

    private List<DoctorScheduleEntity> builderSchedules(){
        return List.of(DoctorScheduleEntity.builder()
                .id(doctorId)
                .dayOfWeek(LocalDate.now().plusDays(2).getDayOfWeek())
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(18, 0))
                .build());
    }

    private List<AppointmentsEntity> builderAppointments(){
        return List.of(AppointmentsEntity.builder()
                .doctorId(doctorId)
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11,0))
                .status(AppointmentsStatus.SCHEDULED)
                .build());
    }

    private List<DoctorTimeBlockEntity> builderTimeBlock(){
        return List.of(DoctorTimeBlockEntity.builder()
                .doctorId(doctorId)
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build());
    }

    private DoctorTimeBlockEntity builderDoctorTimeBlockEntity(DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){
        return DoctorTimeBlockEntity.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .date(doctorTimeBlockRequestDTO.getDate())
                .startTime(doctorTimeBlockRequestDTO.getStartTime())
                .endTime(doctorTimeBlockRequestDTO.getEndTime())
                .build();
    }
}
