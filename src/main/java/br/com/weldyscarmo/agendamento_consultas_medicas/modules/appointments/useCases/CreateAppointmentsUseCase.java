package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidAppointmentDayException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidAppointmentHourException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UnavailableScheduleException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.CreateAppointmentsRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.CreateAppointmentsResponseDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreateAppointmentsUseCase {

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    public CreateAppointmentsResponseDTO execute(UUID patientId, UUID doctorId, CreateAppointmentsRequestDTO createAppointmentsRequestDTO){

        LocalTime endTime = createAppointmentsRequestDTO.getStartTime().plusHours(1);

        DayOfWeek day = createAppointmentsRequestDTO.getDate().getDayOfWeek();

        PatientEntity patientEntity = this.patientRepository.findById(patientId).orElseThrow(() -> {
            throw new UserNotFoundException();
        });
        DoctorEntity doctorEntity = this.doctorRepository.findById(doctorId).orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        List<DoctorScheduleEntity> schedule = doctorAvailable(doctorId, createAppointmentsRequestDTO);
        List<AppointmentsEntity> appointmentsDoctor = this.appointmentsRepository.findAllByDoctorIdAndDay(doctorId,
                day);

        boolean invalidSchedule = true;
        for (DoctorScheduleEntity scheduleEntity : schedule){
            if (!createAppointmentsRequestDTO.getStartTime().isBefore(scheduleEntity.getStartTime())
        && !endTime.isAfter(scheduleEntity.getEndTime())){
                invalidSchedule = false;
                break;
            }
        }

        if (invalidSchedule){
            throw new InvalidAppointmentHourException();
        }

        for (AppointmentsEntity appointments : appointmentsDoctor) {
            if (createAppointmentsRequestDTO.getStartTime().isBefore(appointments.getEndTime())
                    && endTime.isAfter(appointments.getStartTime())
            && appointments.getStatus().equals(AppointmentsStatus.SCHEDULED)
            && appointments.getDate().isEqual(createAppointmentsRequestDTO.getDate())){
                throw new UnavailableScheduleException();
            }
        }

        AppointmentsEntity appointmentsEntity = AppointmentsEntity.builder()
                .date(createAppointmentsRequestDTO.getDate())
                .doctorId(doctorId)
                .patientId(patientId)
                .startTime(createAppointmentsRequestDTO.getStartTime())
                .endTime(endTime)
                .status(AppointmentsStatus.SCHEDULED)
                .build();

        AppointmentsEntity saved = this.appointmentsRepository.save(appointmentsEntity);

        return CreateAppointmentsResponseDTO.builder()
                .id(saved.getId())
                .patientId(saved.getPatientId())
                .doctorId(saved.getDoctorId())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .date(saved.getDate())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private List<DoctorScheduleEntity> doctorAvailable(UUID doctorId,
                                                 CreateAppointmentsRequestDTO createAppointmentsRequestDTO) {

        List<DoctorScheduleEntity> doctorSchedule = this.doctorScheduleRepository.findAllByDoctorId(doctorId);
        List<DoctorScheduleEntity> dailySchedules = new ArrayList<>();

        for (DoctorScheduleEntity schedule : doctorSchedule) {
            if (createAppointmentsRequestDTO.getDate().getDayOfWeek() == schedule.getDayOfWeek()) {
                dailySchedules.add(schedule);
            }
        }
        if (dailySchedules.isEmpty()) {
            throw new InvalidAppointmentDayException();
        }

        return dailySchedules;
    }
}
