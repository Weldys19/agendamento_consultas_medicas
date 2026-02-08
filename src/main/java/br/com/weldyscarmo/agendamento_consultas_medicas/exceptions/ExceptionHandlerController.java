package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerController {

    private MessageSource messageSource;

    public ExceptionHandlerController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> methodArgumentNotValidException(MethodArgumentNotValidException e){
        List<ErrorMessageDTO> errorDTO = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(erro -> {
            String message = messageSource.getMessage(erro, LocaleContextHolder.getLocale());
            ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(erro.getField(), message);
            errorDTO.add(errorMessageDTO);
        });
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(UserFoundException.class)
    public ResponseEntity<String> handlerUserFoundException(UserFoundException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handlerInvalidCredentialsException(InvalidCredentialsException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handlerUserNotFoundException(UserNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidScheduleException.class)
    public ResponseEntity<String> handlerInvalidScheduleException(InvalidScheduleException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDTO> handlerHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.badRequest().body(new ErrorMessageDTO("requestBody",
                "Os campos informados contém valores inválidos"));
    }

    @ExceptionHandler(OverlappingSchedulesException.class)
    public ResponseEntity<String> handlerOverlappingSchedulesException(OverlappingSchedulesException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(InvalidAppointmentDayException.class)
    public ResponseEntity<String> handlerInvalidAppointmentException(InvalidAppointmentDayException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UnavailableScheduleException.class)
    public ResponseEntity<String> handlerUnavailableScheduleException(UnavailableScheduleException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(InvalidAppointmentHourException.class)
    public ResponseEntity<String> handlerInvalidAppointmentHourException(InvalidAppointmentHourException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> handlerInvalidDateException(InvalidDateException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TimeNotFoundException.class)
    public ResponseEntity<String> handlerTimeNotFoundException(TimeNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<String> handlerAppointmentNotFoundException(AppointmentNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidCancellationException.class)
    public ResponseEntity<String> handlerInvalidCancellationException(InvalidCancellationException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ConflictWithSchedulesException.class)
    public ResponseEntity<String> handlerConflictWithSchedulesException(ConflictWithSchedulesException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TimeSlotUnavailableForBlockingException.class)
    public ResponseEntity<String> handlerTimeSlotUnavailableForBlockingException(TimeSlotUnavailableForBlockingException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<String> handlerInvalidRoleException(InvalidRoleException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
