package ru.study.currency_service.clients.utils;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(FeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        var exception = new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ERROR DURING MAKING A REQUEST BY : " + methodKey);

        logger.error(exception.getMessage());

        return exception;
    }
}
