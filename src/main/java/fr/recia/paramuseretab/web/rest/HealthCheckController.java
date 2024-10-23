package fr.recia.paramuseretab.web.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HealthCheckController {

    @GetMapping(value = "/health-check")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Health check. HTTP 200: OK.");
    }

}