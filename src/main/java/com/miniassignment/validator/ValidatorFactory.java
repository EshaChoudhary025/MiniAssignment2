package com.miniassignment.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.miniassignment.service.UserService;
@Component
public class ValidatorFactory {

    @Autowired
    private NumericValidator numericValidator;

    @Autowired
    private EnglishAlphabetsValidator alphabetsValidator;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public <T> InputValidator<T> getValidator(String parameterType) {
        try {
            switch (parameterType.toLowerCase()) {
                case "numeric":
                    logger.info("Using NumericValidator for parameterType: {}", parameterType);
                    return (InputValidator<T>) numericValidator;
                case "alphabets":
                    logger.info("Using EnglishAlphabetsValidator for parameterType: {}", parameterType);
                    return (InputValidator<T>) alphabetsValidator;
                default:
                    throw new IllegalArgumentException("Invalid parameter type");
            }
        } catch (Exception e) {
            logger.error("Error in ValidatorFactory: {}", e.getMessage(), e);
            throw new RuntimeException("Error in ValidatorFactory", e);
        }
    }
}

