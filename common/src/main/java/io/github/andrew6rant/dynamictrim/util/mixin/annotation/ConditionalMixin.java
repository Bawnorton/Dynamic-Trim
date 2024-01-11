package io.github.andrew6rant.dynamictrim.util.mixin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalMixin {
    String modid();
    boolean applyIfPresent() default true;
}
