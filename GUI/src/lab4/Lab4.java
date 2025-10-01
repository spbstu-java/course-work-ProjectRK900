package lab4;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

public class Lab4 {

    public static final class CollectionStreamAPIExamples {
        /**
         * Метод, возвращающий среднее значение списка целых чисел
         */
        public static double average(List<Integer> numbers) {
            return numbers.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElseThrow();
        }

        /**
         * Метод, приводящий все строки в списке в верхний регистр и добавляющий к ним префикс «_new_»
         */
        public static List<String> transformStrings(List<String> strings) {
            return strings.stream()
                    .map(str -> "_new_" + str.toUpperCase())
                    .collect(Collectors.toList());
        }

        /**
         * Метод, возвращающий список квадратов всех встречающихся только один раз элементов списка
         */
        public static List<Integer> squaresOfUnique(List<Integer> numbers) {
            return numbers.stream()
                    .collect(Collectors.groupingBy(
                            num -> num,
                            Collectors.counting()
                    ))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == 1)
                    .map(entry -> entry.getKey() * entry.getKey())
                    .collect(Collectors.toList());
        }

        /**
         * Метод, принимающий на вход коллекцию и возвращающий ее последний элемент или кидающий исключение, если коллекция пуста
         */
        public static <T> T getLastElement(Collection<T> collection) {
            return collection.stream()
                    .reduce((first, second) -> second)
                    .orElseThrow(() -> new NoSuchElementException("Коллекция пуста"));
        }

        /**
         * Метод, принимающий на вход массив целых чисел, возвращающий сумму чётных чисел или 0, если чётных чисел нет
         */
        public static int sumEvenNumbers(int[] numbers) {
            return Arrays.stream(numbers)
                    .filter(num -> num % 2 == 0)
                    .sum();
        }

        /**
         * Метод, преобразовывающий все строки в списке в Map,
         * где первый символ – ключ, оставшиеся – значение
         */
        public static Map<Character, String> stringsToMap(List<String> strings) {
            return strings.stream()
                    .filter(str -> str.length() > 1)
                    .collect(Collectors.toMap(
                            str -> str.charAt(0),                     // Ключ - первый символ
                            str -> str.substring(1),        // Значение - остальные символы
                            (existing, replacement) -> existing // Обработка дубликатов ключей
                    ));
        }

    }

    public static String getSimpleParameterTypes(Method method) {
        Type[] paramTypes = method.getGenericParameterTypes();

        return Arrays.stream(paramTypes)
                .map(type -> {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) type;
                        String rawType = ((Class<?>) pt.getRawType()).getSimpleName();
                        Type[] typeArgs = pt.getActualTypeArguments();

                        if (typeArgs.length > 0) {
                            String genericType = typeArgs[0] instanceof TypeVariable
                                    ? ((TypeVariable<?>) typeArgs[0]).getName()  // T, E, K, ...
                                    : ((Class<?>) typeArgs[0]).getSimpleName();  // String, Integer, ...
                            return rawType + "<" + genericType + ">";
                        }
                        return rawType;
                    }
                    else if (type instanceof Class)
                        return ((Class<?>) type).getSimpleName();
                    else if (type instanceof TypeVariable)
                        return ((TypeVariable<?>) type).getName();

                    return type.getTypeName();
                })
                .collect(Collectors.joining(" "));
    }

    private static Class<?> getListElementType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class)
                return (Class<?>) typeArgs[0];
        }

        return String.class;
    }

    public static String invokeMethodSimple(Method method, Object target, String argsString) throws Exception {
        argsString = argsString.trim();
        Type[] genericParamTypes = method.getGenericParameterTypes();
        Class<?>[] paramClasses = method.getParameterTypes();
        Object[] convertedArgs = new Object[paramClasses.length];

        for (int i = 0; i < paramClasses.length; i++) {
            if (paramClasses[i].isArray()) {
                convertedArgs[i] = parseArray(argsString, paramClasses[i].getComponentType());
            }
            else if (List.class.isAssignableFrom(paramClasses[i]) || Collection.class.isAssignableFrom(paramClasses[i])) {
                Class<?> elementType = getListElementType(genericParamTypes[i]);
                convertedArgs[i] = (elementType == Integer.class) ?
                        parseListInt(argsString) : parseListStr(argsString);
            }
            else
                return "<Не удалось запустить метод>";
        }

        return method.invoke(target, convertedArgs).toString();
    }

    private static Object parseArray(String arg, Class<?> componentType) {
        String[] elements = arg.split(" ");

        if (componentType == int.class) {
            int[] array = new int[elements.length];
            for (int i = 0; i < elements.length; i++)
                array[i] = Integer.parseInt(elements[i].trim());
            return array;
        }
        else if (componentType == Integer.class) {
            Integer[] array = new Integer[elements.length];
            for (int i = 0; i < elements.length; i++)
                array[i] = Integer.valueOf(elements[i].trim());
            return array;
        }

        return elements;
    }

    private static List<Integer> parseListInt(String arg) {
        return Arrays.stream(arg.split(" "))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static List<String> parseListStr(String arg) {
        return Arrays.stream(arg.split(" "))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }
}