/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.config.exception;

import io.cfp.domain.exception.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exception handler
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @Value("${authServer}")
    private String hostname;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        logger.error("Global exception", e);

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotVerifiedException.class)
    public ResponseEntity<Object> handleException(NotVerifiedException e) {
        logger.warn(e.getMessage());

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.FORBIDDEN.value());
        resp.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        resp.setMessage(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleException(AccessDeniedException e) {
        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.FORBIDDEN.value());
        resp.setError(HttpStatus.FORBIDDEN.getReasonPhrase());

        resp.setMessage(e.getMessage());

        logger.error("Accès denied {}", e);
        return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleException(BadRequestException e) {
        logger.warn("Bad request", e);

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        resp.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        resp.setMessage(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleException(NotFoundException e) {
        logger.warn("Not found", e);

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.NOT_FOUND.value());
        resp.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        resp.setMessage(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleException(ForbiddenException e) {
        logger.warn("Forbidden", e);

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.FORBIDDEN.value());
        resp.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        resp.setMessage(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException e) {
        logger.warn("Invalid method argument", e);

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        resp.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        String errorMessage = "";
        String delim = "";
        for (FieldError fieldError : errors) {
            errorMessage+= delim + fieldError.getDefaultMessage();
            delim = ", ";
        }
        resp.setMessage(errorMessage);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CospeakerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleException(CospeakerNotFoundException e) {
        logger.warn("Can't find cospeaker [{}]", e.getCospeaker());

        Map<String, Object> map = new HashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST);
        map.put("errorCode", 1);
        map.put("errorCodeDescription", "CospeakerNotFoundException");
        map.put("errorCodeBody", e.getCospeaker());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleException(EntityExistsException e) {

        ErrorResponse resp = new ErrorResponse(e);
        resp.setStatus(HttpStatus.CONFLICT.value());
        resp.setError(HttpStatus.CONFLICT.getReasonPhrase());
        return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
    }
}
