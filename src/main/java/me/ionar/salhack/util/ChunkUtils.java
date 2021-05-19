package me.ionar.salhack.util;

import java.lang.reflect.Field;

public class ChunkUtils {
    public static Field stealField(Class<?> typeOfClass, Class<?> typeOfField) {
        Field[] fields = typeOfClass.getDeclaredFields();
        Field[] var3 = fields;
        int var4 = fields.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Field f = var3[var5];
            if (f.getType().equals(typeOfField)) {
                try {
                    f.setAccessible(true);
                    return f;
                } catch (Exception var8) {
                    throw new Class41("WorldDownloader: Couldn't steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !", var8);
                }
            }
        }

        throw new Class41("WorldDownloader: Couldn't steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !");
    }

    public static <T> T stealAndGetField(Object object, Class<T> typeOfField) {
        Class typeOfObject;
        if (object instanceof Class) {
            typeOfObject = (Class)object;
            object = null;
        } else {
            typeOfObject = object.getClass();
        }

        try {
            Field f = stealField(typeOfObject, typeOfField);
            return typeOfField.cast(f.get(object));
        } catch (Exception var4) {
            throw new Class41("WorldDownloader: Couldn't get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !", var4);
        }
    }

    public static void stealAndSetField(Object object, Class<?> typeOfField, Object value) {
        Class typeOfObject;
        if (object instanceof Class) {
            typeOfObject = (Class)object;
            object = null;
        } else {
            typeOfObject = object.getClass();
        }

        try {
            Field f = stealField(typeOfObject, typeOfField);
            f.set(object, value);
        } catch (Exception var5) {
            throw new Class41("WorldDownloader: Couldn't set Field of type \"" + typeOfField + "\" from object \"" + object + "\" to " + value + "!", var5);
        }
    }

    public static <T> T stealAndGetField(Object object, Class<?> typeOfObject, Class<T> typeOfField) {
        try {
            Field f = stealField(typeOfObject, typeOfField);
            return typeOfField.cast(f.get(object));
        } catch (Exception var4) {
            throw new Class41("WorldDownloader: Couldn't get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !", var4);
        }
    }

    public static void stealAndSetField(Object object, Class<?> typeOfObject, Class<?> typeOfField, Object value) {
        try {
            Field f = stealField(typeOfObject, typeOfField);
            f.set(object, value);
        } catch (Exception var5) {
            throw new Class41("WorldDownloader: Couldn't set Field of type \"" + typeOfField + "\" from object \"" + object + "\" to " + value + "!", var5);
        }
    }
}
