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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BlockTimeUseCase {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private DoctorTimeBlockRepository doctorTimeBlockRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    public DoctorTimeBlockResponseDTO execute(UUID doctorId, DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){

        if (doctorTimeBlockRequestDTO.getDate().isBefore(LocalDate.now())){
            throw new InvalidDateException();
        }

        if (doctorTimeBlockRequestDTO.getStartTime().isAfter(doctorTimeBlockRequestDTO.getEndTime())){
            throw new InvalidScheduleException();
        }

        //Verifica se o intervalo a ser bloqueado se encaixa nos horários de atendimento
        checkIfTheBreakIsWithinTheWorkingHours(doctorId, doctorTimeBlockRequestDTO);

        //Verifica se o intervalo a ser bloqueado conflita com consulta agendada
        checkIfItConflictsWithAScheduledAppointment(doctorId, doctorTimeBlockRequestDTO);

        //Verifica se o intervalo a ser bloqueado conflita com um horário já bloqueado
        checkIfItConflictsWithAnAlreadyBlockedTimeSlot(doctorId, doctorTimeBlockRequestDTO);

        DoctorTimeBlockEntity doctorTimeBlockEntity = builderDoctorTimeBlockEntity(doctorId, doctorTimeBlockRequestDTO);

        DoctorTimeBlockEntity saved = this.doctorTimeBlockRepository.save(doctorTimeBlockEntity);

        return builderDoctorTimeBlockResponse(saved);
    }

    private void checkIfTheBreakIsWithinTheWorkingHours(UUID doctorId,
                                                           DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){
        List<DoctorScheduleEntity> schedules = this.doctorScheduleRepository
                .findAllByDoctorIdAndDayOfWeek(doctorId, doctorTimeBlockRequestDTO.getDate().getDayOfWeek());

        boolean rangeValid = false;
        for(DoctorScheduleEntity scheduleEntity : schedules) {
            if (!doctorTimeBlockRequestDTO.getStartTime().isBefore(scheduleEntity.getStartTime())
                    && !doctorTimeBlockRequestDTO.getEndTime().isAfter(scheduleEntity.getEndTime())) {
                rangeValid = true;
                break;
            }
        }
        if (!rangeValid){
            throw new TimeSlotUnavailableForBlockingException();
        }
    }

    private void checkIfItConflictsWithAScheduledAppointment(UUID doctorId,
                                                             DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){
        List<AppointmentsEntity> appointments = this.appointmentsRepository
                .findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate());

        for (AppointmentsEntity appointment : appointments){
            if (doctorTimeBlockRequestDTO.getStartTime().isBefore(appointment.getEndTime())
                    && doctorTimeBlockRequestDTO.getEndTime().isAfter(appointment.getStartTime())
                    && appointment.getStatus().equals(AppointmentsStatus.SCHEDULED)){
                throw new ConflictWithSchedulesException();
            }
        }
    }

    private void checkIfItConflictsWithAnAlreadyBlockedTimeSlot(UUID doctorId,
                                                                DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){
        List<DoctorTimeBlockEntity> timesBlock = this.doctorTimeBlockRepository
                .findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate());

        for (DoctorTimeBlockEntity timeBlock : timesBlock) {
            if (doctorTimeBlockRequestDTO.getStartTime().isBefore(timeBlock.getEndTime())
                    && doctorTimeBlockRequestDTO.getEndTime().isAfter(timeBlock.getStartTime())) {
                throw new OverlappingSchedulesException();
            }
        }
    }

    private DoctorTimeBlockEntity builderDoctorTimeBlockEntity(UUID doctorId,
                                                               DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){
        return DoctorTimeBlockEntity.builder()
                .doctorId(doctorId)
                .date(doctorTimeBlockRequestDTO.getDate())
                .startTime(doctorTimeBlockRequestDTO.getStartTime())
                .endTime(doctorTimeBlockRequestDTO.getEndTime())
                .build();
    }

    private DoctorTimeBlockResponseDTO builderDoctorTimeBlockResponse(DoctorTimeBlockEntity doctorTimeBlockEntity){
        return DoctorTimeBlockResponseDTO.builder()
                .id(doctorTimeBlockEntity.getId())
                .doctorId(doctorTimeBlockEntity.getDoctorId())
                .date(doctorTimeBlockEntity.getDate())
                .startTime(doctorTimeBlockEntity.getStartTime())
                .endTime(doctorTimeBlockEntity.getEndTime())
                .build();
    }
}
