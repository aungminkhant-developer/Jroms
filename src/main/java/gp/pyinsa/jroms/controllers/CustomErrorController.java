package gp.pyinsa.jroms.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String showErrorPage(HttpServletRequest request) {
        Object errorStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(errorStatus != null) {
            // There is an error status 
            Integer statusCode = Integer.valueOf(errorStatus.toString());

            if(statusCode.equals(HttpStatus.BAD_REQUEST.value())) {
                return "error-400";
            } else if (statusCode.equals(HttpStatus.FORBIDDEN.value())) {
                return "error-403";
            } else if(statusCode.equals(HttpStatus.NOT_FOUND.value())) {
                return "error-404";
            } else if(statusCode.equals(HttpStatus.METHOD_NOT_ALLOWED.value())) {
                return "error-405";
            } else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR.value())) {
                return "error-500";
            }
        }
        return "error";
    }
}
