package com.things.jopa.persistance.validation;

import com.things.jopa.persistance.exceptions.JopaValidationException;
import com.things.jopa.persistance.mapping.descriptors.ObjectDescription;

public class ObjectDescriptionValidator {
    public void validate(ObjectDescription objectDescription) {
        if (objectDescription.getTableName() == null) {
            throw new JopaValidationException("Unable to find table name of class: [" + objectDescription.getObjectClass() + "]");
        }

        if (objectDescription.getFieldDescriptions().isEmpty()) {
            throw new JopaValidationException("Unable to find field names of class: [" + objectDescription.getObjectClass() + "]");
        }

        if (objectDescription.getFieldDescriptions().stream().noneMatch(ObjectDescription.FieldDescription::getIsKey)) {
            throw new JopaValidationException("Unable to find key in class: [" + objectDescription.getObjectClass() + "]");
        }

        if (objectDescription.getFieldDescriptions().stream().filter(ObjectDescription.FieldDescription::getIsKey).count() > 1) {
            throw new JopaValidationException("More then 1 primary key in class: [" + objectDescription.getObjectClass() + "]");
        }
    }
}
