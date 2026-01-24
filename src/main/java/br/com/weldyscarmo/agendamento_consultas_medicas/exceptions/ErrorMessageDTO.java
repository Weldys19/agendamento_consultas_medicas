package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {

    @Schema(example = "field")
    private String field;

    @Schema(example = "mensagem de erro")
    private String message;
}