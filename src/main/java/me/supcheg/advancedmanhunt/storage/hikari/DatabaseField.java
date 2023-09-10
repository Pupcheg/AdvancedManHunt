package me.supcheg.advancedmanhunt.storage.hikari;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseField {
    String DEFAULT_NAME_RESOLVE = "{default}";

    String value() default DEFAULT_NAME_RESOLVE;

    boolean primary() default false;
}
