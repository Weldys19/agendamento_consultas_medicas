package br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.useCases;

import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.InvalidDayException;
import br.com.weldyscarmo.agendamento_consultas_medicas.exceptions.UserNotFoundException;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.DoctorScheduleRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.CreateDoctorScheduleRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.dtos.DoctorScheduleResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.UUID;


@Service
public class SetOpeningHoursUseCase {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public DoctorScheduleResponseDTO execute(HttpServletRequest request, CreateDoctorScheduleRequestDTO createDoctorScheduleRequestDTO){

        UUID doctorId = UUID.fromString(request.getAttribute("user_id").toString());

        DoctorEntity doctorEntity = this.doctorRepository.findById(doctorId).orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        if (!(createDoctorScheduleRequestDTO.getDayOfWeek() instanceof DayOfWeek)){
            throw new InvalidDayException();
        }
        if (createDoctorScheduleRequestDTO.getStartTime().
                isAfter(createDoctorScheduleRequestDTO.getEndTime())){
            throw new IllegalArgumentException("O horário inical não pode ser menor que o horário final");
        }

        DoctorScheduleEntity doctorScheduleEntity = DoctorScheduleEntity.builder()
                .doctorId(doctorEntity.getId())
                .doctorEntity(doctorEntity)
                .dayOfWeek(createDoctorScheduleRequestDTO.getDayOfWeek())
                .startTime(createDoctorScheduleRequestDTO.getStartTime())
                .endTime(createDoctorScheduleRequestDTO.getEndTime())
                . build();

        DoctorScheduleEntity result = this.doctorScheduleRepository.save(doctorScheduleEntity);

        return DoctorScheduleResponseDTO.builder()
                .id(result.getId())
                .doctorId(result.getDoctorId())
                .doctorEntity(result.getDoctorEntity())
                .dayOfWeek(result.getDayOfWeek())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .build();
    }
}
