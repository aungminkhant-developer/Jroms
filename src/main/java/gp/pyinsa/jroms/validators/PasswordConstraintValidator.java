package gp.pyinsa.jroms.validators;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import gp.pyinsa.jroms.constants.OperationType;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private OperationType opType;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
         opType = constraintAnnotation.operation();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(opType == OperationType.UPDATE && value.isEmpty()) {
            return true;
        }

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 24),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(value));
        if (result.isValid()) {
            return true;
        }

        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }

}
