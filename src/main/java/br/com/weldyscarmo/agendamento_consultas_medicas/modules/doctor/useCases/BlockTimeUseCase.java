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

        List<DoctorScheduleEntity> schedules = this.doctorScheduleRepository
                .findAllByDoctorIdAndDayOfWeek(doctorId, doctorTimeBlockRequestDTO.getDate().getDayOfWeek());

        List<AppointmentsEntity> appointments = this.appointmentsRepository
                .findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate());

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

        for (AppointmentsEntity appointment : appointments){
            if (doctorTimeBlockRequestDTO.getStartTime().isBefore(appointment.getEndTime())
            && doctorTimeBlockRequestDTO.getEndTime().isAfter(appointment.getStartTime())
            && appointment.getStatus().equals(AppointmentsStatus.SCHEDULED)){
                throw new ConflictWithSchedulesException();
            }
        }

        List<DoctorTimeBlockEntity> timesBlock = this.doctorTimeBlockRepository
                .findAllByDoctorIdAndDate(doctorId, doctorTimeBlockRequestDTO.getDate());

        timesBlock.forEach(timeBlock -> {
            if (doctorTimeBlockRequestDTO.getStartTime().isBefore(timeBlock.getEndTime())
            && doctorTimeBlockRequestDTO.getEndTime().isAfter(timeBlock.getStartTime())){
                throw new OverlappingSchedulesException();
            }
        });

        DoctorTimeBlockEntity doctorTimeBlockEntity = DoctorTimeBlockEntity.builder()
                .doctorId(doctorId)
                .date(doctorTimeBlockRequestDTO.getDate())
                .startTime(doctorTimeBlockRequestDTO.getStartTime())
                .endTime(doctorTimeBlockRequestDTO.getEndTime())
                .build();

        DoctorTimeBlockEntity saved = this.doctorTimeBlockRepository.save(doctorTimeBlockEntity);

        return DoctorTimeBlockResponseDTO.builder()
                .id(saved.getId())
                .doctorId(saved.getDoctorId())
                .date(saved.getDate())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .build();
    }
}
