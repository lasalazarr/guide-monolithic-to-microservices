package org.ecuadorjug;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CloudNotTakenValidator implements ConstraintValidator<CloudNotValid, String> {
   public void initialize(CloudNotValid constraint) {
   }

   public boolean isValid(String obj, ConstraintValidatorContext context) {

      return true;
   }
}
