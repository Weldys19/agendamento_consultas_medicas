package br.com.weldyscarmo.agendamento_consultas_medicas.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email/Senha inválido");
    }
}
