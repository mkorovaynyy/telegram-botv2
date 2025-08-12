package pro.sky.telegrambot.exception;

public class InvalidTaskFormatException extends RuntimeException {
    public InvalidTaskFormatException(String message) {
        super(message);
    }
}