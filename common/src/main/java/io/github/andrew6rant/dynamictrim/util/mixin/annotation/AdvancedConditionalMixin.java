package io.github.andrew6rant.dynamictrim.util.mixin.annotation;

import io.github.andrew6rant.dynamictrim.util.mixin.AdvancedConditionChecker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdvancedConditionalMixin {
    Class<? extends AdvancedConditionChecker> checker();
}
