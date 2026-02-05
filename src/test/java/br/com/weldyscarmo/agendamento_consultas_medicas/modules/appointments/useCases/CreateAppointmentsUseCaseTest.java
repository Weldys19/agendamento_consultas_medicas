package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.*;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.AppointmentsResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.CreateAppointmentsRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.*;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateAppointmentsUseCaseTest {

    @InjectMocks
    private CreateAppointmentsUseCase createAppointmentsUseCase;

    @Mock
    private AppointmentsRepository appointmentsRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorTimeBlockRepository doctorTimeBlockRepository;

    UUID patientId;

    @BeforeEach
    void setup(){
        patientId = UUID.randomUUID();
    }

    @Nested
    class WhenDoctorExists{

        DoctorEntity doctorEntity;
        CreateAppointmentsRequestDTO appointmentsRequestDTO;
        LocalTime endTime;
        List<DoctorScheduleEntity> schedules;
        List<AppointmentsEntity> appointments;
        List<DoctorTimeBlockEntity> timesBlock;

        @BeforeEach
        void setup(){
            doctorEntity = DoctorEntity.builder()
                    .id(UUID.randomUUID())
                    .consultationDurationInMinutes(30L)
                    .build();
            when(doctorRepository.findById(doctorEntity.getId()))
                    .thenReturn(Optional.of(doctorEntity));

            appointmentsRequestDTO = builderCreateAppointmentsRequest();

            endTime = appointmentsRequestDTO.getStartTime()
                    .plusMinutes(doctorEntity.getConsultationDurationInMinutes());

            schedules = builderAllDoctorSchedule(doctorEntity);

            appointments = builderAllDoctorAppointments(doctorEntity, appointmentsRequestDTO);

            timesBlock = builderAllDoctorTimeBlock(doctorEntity, appointmentsRequestDTO);
        }

        @Nested
        class AndAppointmentIsValid{

            @Test
            public void shouldCreateAppointmentWhenScheduleIsValid(){

                AppointmentsEntity appointmentsEntity = AppointmentsEntity.builder()
                        .id(UUID.randomUUID())
                        .date(appointmentsRequestDTO.getDate())
                        .doctorId(doctorEntity.getId())
                        .patientId(patientId)
                        .startTime(appointmentsRequestDTO.getStartTime())
                        .endTime(endTime)
                        .status(AppointmentsStatus.SCHEDULED)
                        .build();

                when(appointmentsRepository.findAllByDoctorIdAndDate(doctorEntity.getId(),
                        appointmentsRequestDTO.getDate())).thenReturn(appointments);

                when(doctorScheduleRepository.findAllByDoctorId(doctorEntity.getId()))
                        .thenReturn(schedules);

                when(doctorTimeBlockRepository.findAllByDoctorIdAndDate(doctorEntity.getId(),
                        appointmentsRequestDTO.getDate())).thenReturn(timesBlock);

                when(appointmentsRepository.save(any(AppointmentsEntity.class)))
                        .thenReturn(appointmentsEntity);

                AppointmentsResponseDTO result = createAppointmentsUseCase.execute
                        (patientId, doctorEntity.getId(), appointmentsRequestDTO);

                verify(appointmentsRepository).save(any(AppointmentsEntity.class));
                assertThat(result.getPatientId()).isEqualTo(patientId);
                assertThat(result.getDoctorId()).isEqualTo(doctorEntity.getId());
                assertThat(result.getDate()).isEqualTo(appointmentsRequestDTO.getDate());
                assertThat(result.getStartTime()).isEqualTo(appointmentsRequestDTO.getStartTime());
                assertThat(result.getEndTime()).isEqualTo(endTime);
            }

            @Nested
            class AndAppointmentNIsInvalid{

                @Test
                public void shouldNotCreateANewAppointmentIfTheDateOverlapsWithOneThatHasAlreadyBeenRegistered(){

                    appointmentsRequestDTO.setStartTime(LocalTime.of(10, 0));

                    when(appointmentsRepository.findAllByDoctorIdAndDate(doctorEntity.getId(),
                            appointmentsRequestDTO.getDate())).thenReturn(appointments);

                    when(doctorScheduleRepository.findAllByDoctorId(doctorEntity.getId()))
                            .thenReturn(schedules);

                    assertThatThrownBy(() -> {
                        createAppointmentsUseCase.execute
                                (patientId, doctorEntity.getId(), appointmentsRequestDTO);
                    }).isInstanceOf(UnavailableScheduleException.class);
                }

                @Test
                public void shouldNotCreateAppointmentWhenDateIsBeforeToday(){

                    appointmentsRequestDTO.setDate(LocalDate.now().minusDays(2));

                    assertThatThrownBy(() -> {
                        createAppointmentsUseCase.execute(patientId, doctorEntity.getId(),
                                appointmentsRequestDTO);
                    }).isInstanceOf(InvalidDateException.class);
                }

                @Test
                public void shouldNotCreateAppointmentWhenDoctorDoesNotWorkOnThatDay(){

                    schedules.getFirst().setDayOfWeek(LocalDate.now().getDayOfWeek());

                    when(doctorScheduleRepository.findAllByDoctorId(doctorEntity.getId()))
                            .thenReturn(schedules);

                    assertThatThrownBy(() -> {
                        createAppointmentsUseCase.execute(patientId, doctorEntity.getId(),
                                appointmentsRequestDTO);
                    }).isInstanceOf(InvalidAppointmentDayException.class);
                }

                @Test
                public void shouldNotCreateAppointmentWhenDoctorIsNotAvailableAtThatTime(){

                    appointmentsRequestDTO.setStartTime(LocalTime.of(7, 0));

                    when(doctorScheduleRepository.findAllByDoctorId(doctorEntity.getId()))
                            .thenReturn(schedules);

                    assertThatThrownBy(() -> {
                        createAppointmentsUseCase.execute(patientId, doctorEntity.getId(),
                                appointmentsRequestDTO);
                    }).isInstanceOf(InvalidAppointmentHourException.class);
                }

                @Test
                public void shouldNotCreateAppointmentWhenDoctorHasBlockedTime(){

                    appointmentsRequestDTO.setStartTime(LocalTime.of(15, 0));

                    when(doctorScheduleRepository.findAllByDoctorId(doctorEntity.getId()))
                            .thenReturn(schedules);

                    when(appointmentsRepository.findAllByDoctorIdAndDate(doctorEntity.getId(),
                            appointmentsRequestDTO.getDate())).thenReturn(appointments);

                    when(doctorTimeBlockRepository.findAllByDoctorIdAndDate
                            (doctorEntity.getId(), appointmentsRequestDTO.getDate())).thenReturn(timesBlock);

                    assertThatThrownBy(() -> {
                        createAppointmentsUseCase.execute(patientId, doctorEntity.getId(), appointmentsRequestDTO);
                    }).isInstanceOf(UnavailableScheduleException.class);
                }
            }
        }
    }

    @Nested
    class WhenDoctorNotExists{

        @Test
        public void shouldNotCreateAppointmentWhenDoctorDoesNotExist(){

            when(doctorRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                createAppointmentsUseCase.execute(patientId, UUID.randomUUID(),
                        new CreateAppointmentsRequestDTO());
            }).isInstanceOf(UserNotFoundException.class);
        }
    }

    private CreateAppointmentsRequestDTO builderCreateAppointmentsRequest(){
        return CreateAppointmentsRequestDTO.builder()
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(9, 0))
                .build();
    }

    private List<DoctorScheduleEntity> builderAllDoctorSchedule(DoctorEntity doctorEntity){
        return List.of(DoctorScheduleEntity.builder()
                .doctorId(doctorEntity.getId())
                .dayOfWeek(LocalDate.now().plusDays(2).getDayOfWeek())
                .startTime(LocalTime.of(8,0))
                .endTime(LocalTime.of(18, 0))
                .build());
    }

    private List<AppointmentsEntity> builderAllDoctorAppointments(DoctorEntity doctorEntity,
                                                                  CreateAppointmentsRequestDTO createAppointmentsRequestDTO) {
        return List.of(AppointmentsEntity.builder()
                .doctorId(doctorEntity.getId())
                .date(createAppointmentsRequestDTO.getDate())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .status(AppointmentsStatus.SCHEDULED)
                .build());
    }

    private List<DoctorTimeBlockEntity> builderAllDoctorTimeBlock
            (DoctorEntity doctorEntity, CreateAppointmentsRequestDTO createAppointmentsRequestDTO){
        return List.of(DoctorTimeBlockEntity.builder()
                .doctorId(doctorEntity.getId())
                .date(createAppointmentsRequestDTO.getDate())
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(17, 0))
                .build());
    }
}
