package mate.academy.carsharingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanUtils;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object firstValue = Objects.requireNonNull(
                    BeanUtils.getPropertyDescriptor(value.getClass(), firstFieldName))
                    .getReadMethod().invoke(value);
            Object secondValue = Objects.requireNonNull(
                    BeanUtils.getPropertyDescriptor(value.getClass(), secondFieldName))
                    .getReadMethod().invoke(value);

            return firstValue == null && secondValue == null || (firstValue != null
                    && firstValue.equals(secondValue));
        } catch (Exception ignore) {
            return false;
        }
    }
}

