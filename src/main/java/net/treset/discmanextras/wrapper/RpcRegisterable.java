package net.treset.discmanextras.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public static method to be called to register Minecraft Server Management RPC extension features.
 * The {@code priority} property can be used if dependencies exist. Higher priorities are registered first.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcRegisterable {
    int priority() default -1;
}
