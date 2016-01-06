package org.facil.practice;

import java.lang.reflect.Field;

/**
 * Created by sumanthdommaraju on 1/1/16.
 */
public class ReflectionUtils {

    public static <T,U> void setNonAccesibleField(Class<?> clazz, T obj, String fieldName, U value) throws IllegalAccessException, NoSuchFieldException {
        if(clazz == null){
            throw new NoSuchFieldException();
        }
        try{
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
            return;
        }catch (NoSuchFieldException nsfe){
            setNonAccesibleField(clazz.getSuperclass(), obj, fieldName, value);
        }
    }
}
