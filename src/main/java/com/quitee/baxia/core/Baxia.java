package com.quitee.baxia.core;

import com.quitee.baxia.exceptions.NoSuchSpace;
import com.quitee.baxia.exceptions.NotASpaceClass;
import com.quitee.baxia.exceptions.NotExtendsFromRefClass;
import com.quitee.baxia.scan.BxScaner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Baxia {
    public static void scan(String scanPattern) {
        new BxScaner(aClass -> {
            if (aClass.getAnnotation(com.quitee.baxia.annotations.Space.class) != null){
                createSpace(aClass, (com.quitee.baxia.annotations.Space) aClass.getAnnotation(com.quitee.baxia.annotations.Space.class));
            }

        }).scan(scanPattern);
    }

    private static Map<Class, Space> spaceMap = new HashMap<>();

    private static synchronized Space createSpace(Class aClass,com.quitee.baxia.annotations.Space spaceA) {
        if (!spaceMap.containsKey(aClass)) {
            Class refClass = spaceA.ref();
            if(refClass.equals(com.quitee.baxia.annotations.Space.NullClass.class)){
                Space space = new SpaceImp(aClass);
                spaceMap.put(aClass, space);
            }else {
                if (refClass.isAssignableFrom(aClass)){
                    if (refClass.getAnnotation(com.quitee.baxia.annotations.Space.class) != null){
                        createSpace(refClass, (com.quitee.baxia.annotations.Space) refClass.getAnnotation(com.quitee.baxia.annotations.Space.class));
                        spaceMap.put(aClass,getSpace(refClass));
                    }else {
                        throw new NotASpaceClass(refClass);
                    }
                }else {
                    throw new NotExtendsFromRefClass(aClass,refClass);
                }
            }
        }
        return (spaceMap.get(aClass));
    }

    public static synchronized <T> T get(Class<T> tClass, String where, Object value, Object... parameters) {
        Space<T> space = getSpace(tClass);
        return space.get(value, where, parameters);
    }

    public static synchronized <T> List<T> getList(Class<T> tClass, String where, Object value,Object... parameters) {
        Space<T> space = getSpace(tClass);
        return space.getList(value, where, parameters);
    }

    public static synchronized <T> List<T> getList(Class<T> tClass, String where, List<Object> values, Object... parameters) {
        Space<T> space = getSpace(tClass);
        return space.getList(values, where, parameters);
    }

    public static synchronized void add(Object o) {
        getSpace(o.getClass()).add(o);
    }

    public static synchronized void remove(Object o) {
        getSpace(o.getClass()).remove(o);
    }

    private static synchronized Space getSpace(Class tClass) {
        Space space = spaceMap.get(tClass);
        if (space != null) {
            return space;
        } else
            throw new NoSuchSpace(tClass);
    }

}
