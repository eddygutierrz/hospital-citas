package kosmos.java.com.hospital.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(
                "error", "Conflicto en la solicitud",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(Map.of(
                "error", "Datos inválidos",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
    }
    
}