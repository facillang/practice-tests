package org.facil.practice;

import java.lang.reflect.Field;

/**
 * Created by sumanthdommaraju on 1/1/16.
 */
public class ReflectionUtils {

    public static void setNonAccessibleField(Object object, String fieldName, Object value) throws IllegalAccessException,
                                                                                    NoSuchFieldException {
        Field field = getField(object.getClass(), fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = null;
        for(Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()){
            try{
                field = cls.getDeclaredField(fieldName);
            }catch (NoSuchFieldException nsme){
                //If not ignored cannot walk through class hierarchy
            }
        }
        if(field == null){
            throw new NoSuchFieldException();
        }
        return field;
    }
}
