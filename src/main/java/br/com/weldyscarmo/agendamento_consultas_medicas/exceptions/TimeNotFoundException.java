package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class TimeNotFoundException extends RuntimeException {
    public TimeNotFoundException() {
        super("Horário não encontrado");
    }
}
