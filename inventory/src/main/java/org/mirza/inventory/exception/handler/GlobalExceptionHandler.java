package org.mirza.inventory.exception.handler;

import org.mirza.entity.dto.BaseResponse;
import org.mirza.inventory.exception.GlobalException;
import org.mirza.inventory.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGlobalException(Exception ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setCode(500);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle ResourceNotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(
            NotFoundException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle GlobalException
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<BaseResponse<Object>> handleGlobalException(
            GlobalException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}