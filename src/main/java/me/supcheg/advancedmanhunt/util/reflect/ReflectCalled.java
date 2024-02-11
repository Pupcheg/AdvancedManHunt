package me.supcheg.advancedmanhunt.util.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see MethodHandleLookup
 * @see InstantMethodHandleLookup
 */
@Target({
        ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
        ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE, ElementType.PACKAGE, ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE, ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectCalled {
}
