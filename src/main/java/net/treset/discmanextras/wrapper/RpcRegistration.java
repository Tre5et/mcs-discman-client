package net.treset.discmanextras.wrapper;

import net.treset.discmanextras.DiscmanExtrasMod;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class RpcRegistration {
    public static void applyRegister() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(""))
                .setScanners(Scanners.MethodsAnnotated));

        Set<Method> res = reflections
                .getMethodsAnnotatedWith(RpcRegisterable.class);
        List<Method> methods = res.stream()
                .map(method -> Map.entry(method.getAnnotation(RpcRegisterable.class).priority(), method))
                .sorted((e1,e2) -> e2.getKey() - e1.getKey())
                .map(Map.Entry::getValue)
                .toList();

        methods.forEach(m -> {
            if(!Modifier.isPublic(m.getModifiers())) {
                throw new IllegalStateException("RPC registering methods must be public: " + m.getDeclaringClass().getName() + "$" + m.getName());
            }
            if(!Modifier.isStatic(m.getModifiers())) {
                throw new IllegalStateException("RPC registering methods must be static: " + m.getDeclaringClass().getName() + "$" + m.getName());
            }
            if(m.getParameterCount() > 0) {
                throw new IllegalStateException("RPC registering method has parameters: " + m.getDeclaringClass().getName() + "$" + m.getName());
            }
            try {
                m.invoke(m.getName());
                DiscmanExtrasMod.LOGGER.info("Registered RPC Method: " + m.getDeclaringClass().getSimpleName() + "$" + m.getName() + "()");
            } catch (Exception e) {
                throw new IllegalStateException("Error in RPC registering method " + m.getDeclaringClass().getName() + "$" + m.getName(), e);
            }
        });

        System.exit(0);
    }
}
