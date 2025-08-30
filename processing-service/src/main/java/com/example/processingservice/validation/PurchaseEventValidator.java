package com.example.processingservice.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class PurchaseEventValidator {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public <T> boolean validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            // Log with details
            violations.forEach(v ->
                    log.warn("Validation failed for {}.{}: {}",
                            v.getRootBeanClass().getSimpleName(),
                            v.getPropertyPath(),
                            v.getMessage())
            );
            return false; // indicate invalid
        }
        return true;
    }
}
