package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.AppointmentNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidCancellationException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.AppointmentsResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CancelAppointmentUseCaseTest {

    @InjectMocks
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @Mock
    private AppointmentsRepository appointmentsRepository;

    @Nested
    class WhenAppointmentExists{

        String role;
        UUID userId;
        UUID appointmentId;
        AppointmentsEntity appointmentsEntity;

        @BeforeEach
        void setup(){
            role = "ROLE_DOCTOR";
            userId = UUID.randomUUID();
            appointmentId = UUID.randomUUID();
            appointmentsEntity = builderAppointment();

            when(appointmentsRepository.findByIdAndDoctorId(appointmentId, userId))
                    .thenReturn(Optional.of(appointmentsEntity));
        }

        @Test
        public void shouldCancelAppointmentWhenItExistsAndIsAtLeastTwoHoursBefore(){

            AppointmentsResponseDTO result = cancelAppointmentUseCase.execute(role, userId, appointmentId);

            assertThat(result.getStatus()).isEqualTo(AppointmentsStatus.CANCELED);
        }

        @Test
        public void shouldNotCancelAppointmentWhenMinimumTwoHoursDeadlineHasPassed(){

            appointmentsEntity.setDate(LocalDate.now());
            appointmentsEntity.setStartTime(LocalTime.now().plusHours(1));

            assertThatThrownBy(() -> {
                cancelAppointmentUseCase.execute(role, userId, appointmentId);
            }).isInstanceOf(InvalidCancellationException.class);
        }
    }

    @Nested
    class WhenAppointmentNotExists{

        @Test
        public void shouldNotCancelAppointmentWhenItDoesNotExist(){

            when(appointmentsRepository.findByIdAndDoctorId(any(UUID.class), any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
                cancelAppointmentUseCase.execute("ROLE_DOCTOR", UUID.randomUUID(), UUID.randomUUID());
            }).isInstanceOf(AppointmentNotFoundException.class);
        }
    }

    private AppointmentsEntity builderAppointment(){
        return AppointmentsEntity.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(12,0))
                .build();
    }
}
