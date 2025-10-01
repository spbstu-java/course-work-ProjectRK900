package lab2;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.Arrays;


public class Lab2 {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Repeat {
        int value();
    }

    public static final class AnnotationTestClass {
        @Repeat(-1)
        public String publicMethod1(String msg) {
            return "publicMethod1: " + msg;
        }

        @Repeat(0)
        public String publicMethod2(int a, int b) {
            return "publicMethod2: " + (a + b);
        }

        @Repeat(3)
        protected String protectedMethod1() {
            return "protectedMethod1 has no parameters";
        }

        @Repeat(2)
        protected String protectedMethod2(String place) {
            return "protectedMethod2: place - " + place;
        }

        @Repeat(4)
        private String privateMethod1(int x) {
            return "privateMethod1: integer number = " + x;
        }

        private String privateMethod2() {
            return "privateMethod2 has no annotation";
        }
    }

    private static Object[] getDefaultArgs(Class<?>[] paramTypes) {
        var args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].isPrimitive())
                args[i] = 0;
            else
                args[i] = null;
        }

        return args;
    }

    public static String executeMethod(AnnotationTestClass obj, Method method) {
        var modifier = method.getModifiers();
        if (Modifier.isPrivate(modifier) || Modifier.isProtected(modifier))
            method.setAccessible(true);

        var argsForMethod = getDefaultArgs(method.getParameterTypes());
        int times = method.isAnnotationPresent(Repeat.class) ?
                method.getAnnotation(Repeat.class).value() : 1;
        var outputStrBuild = new StringBuilder();
        try {
            for (int i = 0; i < times; i++)
                outputStrBuild.append(method.invoke(obj, argsForMethod)).append("\n");
        }
        catch (Exception e) {
            outputStrBuild.append(Arrays.toString(e.getStackTrace()));
        }

        return outputStrBuild.toString();
    }

    public static String beginAll(AnnotationTestClass obj) {
        var outputStrBuild = new StringBuilder();
        for (Method method : obj.getClass().getDeclaredMethods()) {
            var modifier = method.getModifiers();
            if (method.isAnnotationPresent(Repeat.class) && (Modifier.isPrivate(modifier) || Modifier.isProtected(modifier))) {
                method.setAccessible(true);
                var argsForMethod = getDefaultArgs(method.getParameterTypes());

                int times = method.getAnnotation(Repeat.class).value();
                try {
                    for (int i = 0; i < times; i++)
                        outputStrBuild.append(method.invoke(obj, argsForMethod)).append("\n");
                }
                catch (Exception e) {
                    outputStrBuild.append(Arrays.toString(e.getStackTrace()));
                }
            }
        }

        return outputStrBuild.toString();
    }
}