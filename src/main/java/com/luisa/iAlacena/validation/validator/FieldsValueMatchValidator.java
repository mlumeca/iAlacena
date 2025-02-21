package com.luisa.iAlacena.validation.validator;

import com.luisa.iAlacena.validation.annotation.FieldsValueMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.PropertyAccessorFactory;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch, Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = PropertyAccessorFactory
                .forBeanPropertyAccess(o).getPropertyValue(field);
        Object fieldValueMatch = PropertyAccessorFactory
                .forBeanPropertyAccess(o).getPropertyValue(field);

        if (fieldValue != null)
            return fieldMatch.equals(fieldValueMatch);
        else
            return fieldValueMatch == null;

    }
}