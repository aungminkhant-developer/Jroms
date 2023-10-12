package gp.pyinsa.jroms.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gp.pyinsa.jroms.constants.OperationType;

public class PasswordConstraintValidatorUserUpdate implements ConstraintValidator<ValidPassword, String> {
    private OperationType operation;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.operation = constraintAnnotation.operation();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return operation == OperationType.UPDATE;
        }

        // Check password length
        if (password.length() < 8 || password.length() > 20) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for at least one lowercase letter
        return password.matches(".*[a-z].*");
    }
}

