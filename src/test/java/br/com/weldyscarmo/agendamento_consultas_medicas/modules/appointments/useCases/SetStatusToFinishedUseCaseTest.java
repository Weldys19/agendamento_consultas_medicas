package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.AppointmentNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.AppointmentsResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SetStatusToFinishedUseCaseTest {

    @InjectMocks
    private SetStatusToFinishedUseCase setStatusToFinishedUseCase;

    @Mock
    private AppointmentsRepository appointmentsRepository;

    UUID doctorId;
    UUID appointmentId;
    AppointmentsEntity appointmentsEntity;

    @BeforeEach
    void setup(){
        doctorId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        appointmentsEntity = builderAppointments();
    }

    @Test
    public void shouldChangeStatusToFinishedWhenAppointmentExists(){

        when(appointmentsRepository.findByIdAndDoctorId(appointmentId, doctorId))
                .thenReturn(Optional.of(appointmentsEntity));

        AppointmentsResponseDTO result = setStatusToFinishedUseCase.execute(doctorId, appointmentId);

        assertThat(result.getStatus()).isEqualTo(AppointmentsStatus.FINISHED);
    }

    @Test
    public void shouldNotChangeStatusWhenAppointmentDoesNotExist(){

        when(appointmentsRepository.findByIdAndDoctorId(appointmentId, doctorId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            setStatusToFinishedUseCase.execute(doctorId, appointmentId);
        }).isInstanceOf(AppointmentNotFoundException.class);
    }

    private AppointmentsEntity builderAppointments(){
        return AppointmentsEntity.builder()
                .id(appointmentId)
                .doctorId(doctorId)
                .status(AppointmentsStatus.SCHEDULED)
                .build();
    }
}
