package lab3;

import java.io.IOException;

public class FileReadException extends IOException {
    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}