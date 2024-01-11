package io.github.andrew6rant.dynamictrim.util.mixin;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface AdvancedConditionChecker {
    MethodHandles.Lookup lookup = MethodHandles.lookup();

    boolean shouldApply();

    static AdvancedConditionChecker create(Type checkerType) {
        try {
            Class<?> clazz = Class.forName(checkerType.getClassName());
            MethodHandle constructorHandle = lookup.findConstructor(clazz, MethodType.methodType(void.class));
            if (!(constructorHandle.invoke() instanceof AdvancedConditionChecker checker)) {
                throw new RuntimeException("AdvancedConditionChecker class " + checkerType.getClassName() + " does not implement AdvancedConditionChecker");
            }

            return checker;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
