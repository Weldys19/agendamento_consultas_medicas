package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class InvalidTimeExclusionException extends RuntimeException {
    public InvalidTimeExclusionException() {
        super("Não pode excluir um horário que não pertença ao usuário autenticado");
    }
}
