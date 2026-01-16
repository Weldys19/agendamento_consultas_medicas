package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {

    private String field;
    private String message;
}