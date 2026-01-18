package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handlerInvalidCredentialsException(InvalidCredentialsException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
