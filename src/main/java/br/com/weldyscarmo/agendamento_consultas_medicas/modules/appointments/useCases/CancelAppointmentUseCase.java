package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.AppointmentNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidCancellationException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidRoleException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.MapperAppointmentResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.AppointmentsResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CancelAppointmentUseCase {

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Transactional
    public AppointmentsResponseDTO execute(String role, UUID userId, UUID appointmentId){

        AppointmentsEntity appointment = checkAPatientOrADoctor(role, appointmentId, userId);

        LocalDateTime dateConsultation = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());

        if (LocalDateTime.now().isAfter(dateConsultation.minusHours(2))) {
            throw new InvalidCancellationException();
        }

        appointment.setStatus(AppointmentsStatus.CANCELED);

        return MapperAppointmentResponseDTO.mapperAppointment(appointment);
    }

    private AppointmentsEntity checkAPatientOrADoctor(String role, UUID appointmentId, UUID userId){

        AppointmentsEntity appointment = null;

        if (role.equals("ROLE_DOCTOR")) {
            appointment = this.appointmentsRepository.findByIdAndDoctorId(appointmentId, userId)
                    .orElseThrow(() -> {
                        throw new AppointmentNotFoundException();
                    });

        } else if (role.equals("ROLE_PATIENT")){
            appointment = this.appointmentsRepository.findByIdAndPatientId(appointmentId, userId)
                    .orElseThrow(() -> {
                        throw new AppointmentNotFoundException();
                    });
        }

        if (appointment == null){
            throw new InvalidRoleException();
        }
        return appointment;
    }
}
