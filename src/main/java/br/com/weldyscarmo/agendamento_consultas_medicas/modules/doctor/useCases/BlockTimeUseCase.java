package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidDateException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidScheduleException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.OverlappingSchedulesException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.TimeSlotUnavailableForBlockingException;
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

    public DoctorTimeBlockResponseDTO execute(UUID doctorId, DoctorTimeBlockRequestDTO doctorTimeBlockRequestDTO){

        if (doctorTimeBlockRequestDTO.getDate().isBefore(LocalDate.now())){
            throw new InvalidDateException();
        }

        if (doctorTimeBlockRequestDTO.getStartTime().isAfter(doctorTimeBlockRequestDTO.getEndTime())){
            throw new InvalidScheduleException();
        }

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
