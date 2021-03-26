package com.bdb.lottery.utils.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Reflections {
    private static int COUNT_MAX_RECURSIVE = 3;

    public static Type getGenericActualTypeArg(Class clazz) {
        return getGenericSuperclassActualTypeArg(clazz);
    }

    /**
     * 递归第一个接口
     *
     * @param clazz
     * @return
     */
    public static Type getGenericInterfacesActualTypeArg(Class clazz) {
        int count = COUNT_MAX_RECURSIVE;
        while (null != clazz && count > 0) {
            count--;
            Type type = null;
            Type[] types = clazz.getGenericInterfaces();
            if (null != types && types.length > 0)
                type = types[0];
            if (null == type) return null;
            if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (null != actualTypeArguments && actualTypeArguments.length > 0)
                    return actualTypeArguments[0];
            }
            clazz = type.getClass();
        }
        return null;
    }

    /**
     * 递归第一个接口
     *
     * @param clazz
     * @return
     */
    public static Type getGenericSuperclassActualTypeArg(Class clazz) {
        int count = COUNT_MAX_RECURSIVE;
        while (null != clazz && count > 0) {
            count--;
            Type type = clazz.getGenericSuperclass();
            if (null == type) return null;
            if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if (null != actualTypeArguments && actualTypeArguments.length > 0)
                    return actualTypeArguments[0];
            }
            clazz = type.getClass();
        }
        return null;
    }
}
