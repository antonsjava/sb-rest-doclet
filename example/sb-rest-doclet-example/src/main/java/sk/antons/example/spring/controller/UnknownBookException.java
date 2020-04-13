/*
 * 
 */
package sk.antons.example.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Unidentified book state.
 * @author antons
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UnknownBookException extends Exception {
    
}
