package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class UserFoundException extends RuntimeException {
    public UserFoundException() {
        super("Usuario já existe");
    }
}
