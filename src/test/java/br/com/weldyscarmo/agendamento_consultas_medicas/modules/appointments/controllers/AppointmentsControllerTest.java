package br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.controllers;

import br.com.weldyscarmo.agendamento_consultas_medicas.enums.AppointmentsStatus;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.AppointmentsRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.appointments.dtos.CreateAppointmentsRequestDTO;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.doctor.*;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientEntity;
import br.com.weldyscarmo.agendamento_consultas_medicas.modules.patient.PatientRepository;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.JWTGenerate;
import br.com.weldyscarmo.agendamento_consultas_medicas.security.dtos.AuthUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class AppointmentsControllerTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private DoctorTimeBlockRepository doctorTimeBlockRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTGenerate jwtGenerate;

    private MockMvc mockMvc;

    private final LocalDate DATE_TEST = LocalDate.now().plusDays(2);

    PatientEntity patientEntity;
    DoctorEntity doctorEntity;
    DoctorScheduleEntity doctorScheduleEntity;
    DoctorTimeBlockEntity doctorTimeBlockEntity;
    AppointmentsEntity appointmentsEntity;
    CreateAppointmentsRequestDTO createAppointmentsRequestDTO;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        createAppointmentsRequestDTO = builderAppointmentRequest();

        patientEntity = builderPatientEntity();
        patientEntity = patientRepository.saveAndFlush(patientEntity);

        doctorEntity = builderDoctorEntity();
        doctorEntity = doctorRepository.saveAndFlush(doctorEntity);

        doctorScheduleEntity = builderDoctorSchedule(doctorEntity.getId());
        doctorScheduleEntity = doctorScheduleRepository.saveAndFlush(doctorScheduleEntity);

        doctorTimeBlockEntity = builderDoctorTimeBlock(doctorEntity.getId());
        doctorTimeBlockEntity = doctorTimeBlockRepository.saveAndFlush(doctorTimeBlockEntity);

        appointmentsEntity = builderAppointments(patientEntity.getId(), doctorEntity.getId());
        appointmentsEntity = appointmentsRepository.saveAndFlush(appointmentsEntity);
    }

    @Nested
    class WhenTheDataIsValid{

        @Test
        public void shouldCreateAppointmentSuccessfully() throws Exception {
            mockMvcRequest(doctorEntity.getId(), createAppointmentsRequestDTO, 201);
        }
    }

    @Nested
    class WhenTheDataIsInvalid{

        @Test
        public void shouldNotCreateAppointmentWhenDoctorNotExists() throws Exception{
            mockMvcRequest(UUID.randomUUID(), createAppointmentsRequestDTO, 404);
        }

        @Test
        public void shouldNotCreateAppointmentWhenTheDateIsBeforeTheDateNow() throws Exception{
            createAppointmentsRequestDTO.setDate(LocalDate.now().minusDays(1));
            mockMvcRequest(doctorEntity.getId(), createAppointmentsRequestDTO, 409);
        }

        @Test
        public void shouldNotCreateAppointmentWhenDoctorNotWorkingTheDay() throws Exception{
            createAppointmentsRequestDTO.setDate(LocalDate.now().plusDays(1));
            mockMvcRequest(doctorEntity.getId(), createAppointmentsRequestDTO, 400);
        }

        @Test
        public void shouldNotCreateAppointmentWhenDoctorNotWorkingTheHour() throws Exception{
            createAppointmentsRequestDTO.setStartTime(LocalTime.of(6, 0));
            mockMvcRequest(doctorEntity.getId(), createAppointmentsRequestDTO, 400);
        }

        @Test
        public void shouldNotCreateAppointmentWhenAlreadyExistsARegistered() throws Exception{
            createAppointmentsRequestDTO.setStartTime(LocalTime.of(11, 0));
            mockMvcRequest(doctorEntity.getId(), createAppointmentsRequestDTO, 409);
        }

        @Test
        public void shouldNotCreateAppointmentWhenTheHourIsBlockedTheDoctor() throws Exception{
            createAppointmentsRequestDTO.setStartTime(LocalTime.of(7, 0));
            mockMvcRequest(doctorEntity.getId(), createAppointmentsRequestDTO, 409);
        }
    }

    private void mockMvcRequest(UUID doctorId, CreateAppointmentsRequestDTO createAppointmentsRequestDTO,
                                int expectStatus) throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/appointments/{doctorId}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAppointmentsRequestDTO))
                        .header("Authorization", "Bearer " + tokenGenerated()))
                .andExpect(MockMvcResultMatchers.status().is(expectStatus));
    }

    private String tokenGenerated(){
        AuthUserDTO authUser = builderAuthUser(patientEntity);
        return jwtGenerate.generateToken(authUser).getToken();
    }

    private AuthUserDTO builderAuthUser(PatientEntity patientEntity){
        return AuthUserDTO.builder()
                .id(patientEntity.getId())
                .email(patientEntity.getEmail())
                .password(patientEntity.getPassword())
                .roles("PATIENT")
                .build();
    }

    private PatientEntity builderPatientEntity(){
        return PatientEntity.builder()
                .name("weldys")
                .username("weldys002")
                .email("weldys@gmail.com")
                .password("1234567890")
                .build();
    }

    private DoctorEntity builderDoctorEntity(){
        return DoctorEntity.builder()
                .name("welington")
                .specialty("Urologista")
                .email("welington@gmail.com")
                .password("1234567890")
                .consultationDurationInMinutes(60L)
                .build();
    }

    private CreateAppointmentsRequestDTO builderAppointmentRequest(){
        return CreateAppointmentsRequestDTO.builder()
                .date(DATE_TEST)
                .startTime(LocalTime.of(15,0))
                .build();
    }

    private DoctorScheduleEntity builderDoctorSchedule(UUID doctorId){
        return DoctorScheduleEntity.builder()
                .doctorId(doctorId)
                .dayOfWeek(DATE_TEST.getDayOfWeek())
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(20, 0))
                .build();
    }

    private DoctorTimeBlockEntity builderDoctorTimeBlock(UUID doctorId){
        return DoctorTimeBlockEntity.builder()
                .doctorId(doctorId)
                .date(DATE_TEST)
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(10,0))
                .build();
    }

    private AppointmentsEntity builderAppointments(UUID patientId, UUID doctorId){
        return AppointmentsEntity.builder()
                .patientId(patientId)
                .doctorId(doctorId)
                .date(DATE_TEST)
                .startTime(LocalTime.of(11,0))
                .endTime(LocalTime.of(12,0))
                .status(AppointmentsStatus.SCHEDULED)
                .build();
    }
}
