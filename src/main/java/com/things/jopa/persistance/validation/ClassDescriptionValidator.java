package com.things.jopa.persistance.validation;

import com.things.jopa.persistance.exceptions.JopaValidationException;
import com.things.jopa.persistance.mapping.descriptors.ClassDescription;

public class ClassDescriptionValidator {
    public void validate(ClassDescription classDescription) {
        if (classDescription.getTableName() == null) {
            throw new JopaValidationException("Unable to find table name of class: [" + classDescription.getClazz() + "]");
        }

        if (classDescription.getFieldDescriptions().isEmpty()) {
            throw new JopaValidationException("Unable to find field names of class: [" + classDescription.getClazz() + "]");
        }

        if (classDescription.getFieldDescriptions().stream().noneMatch(ClassDescription.FieldDescription::getIsKey)) {
            throw new JopaValidationException("Unable to find key in class: [" + classDescription.getClazz() + "]");
        }

        if (classDescription.getFieldDescriptions().stream().filter(ClassDescription.FieldDescription::getIsKey).count() > 1) {
            throw new JopaValidationException("More then 1 primary key in class: [" + classDescription.getClazz() + "]");
        }
    }
}
