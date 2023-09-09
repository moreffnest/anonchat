package com.moreffnest.anonchat.exceptions;

import com.moreffnest.parsers.exceptions.InvalidFileExtensionException;
import com.moreffnest.parsers.exceptions.InvalidListPageException;
import com.moreffnest.parsers.exceptions.InvalidListTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

    @ExceptionHandler(AnonchatException.class)
    public String handleAnonchatException(AnonchatException ex, WebRequest request) {
        return "error/anonchat_error";
    }

    @ExceptionHandler({InvalidFileExtensionException.class, InvalidListPageException.class,
            InvalidListTypeException.class})
    public String handleListsException(Exception ex, WebRequest request) {
        log.warn("User {} submitted invalid input", request.getUserPrincipal().getName(), ex); //these exceptions happen only for authenticated users
        return "error/list_error";
    }

    @ExceptionHandler(ApplicationCancelledException.class)
    public String handleApplicationCancelledException(ApplicationCancelledException ex, WebRequest request) {
        log.info("User with the session id {} cancelled his application", request.getSessionId(), ex);
        return "redirect:/chatform";
    }

}
