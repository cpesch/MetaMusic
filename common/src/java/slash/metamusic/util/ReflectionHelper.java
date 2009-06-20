/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;

/**
 * With the help of reflection allows to
 * <ul>
 * <li>create objects without default constructor</li>
 * <li>get non-public fields</li>
 * <li>get non-public methods</li>
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: ReflectionHelper.java 439 2004-12-08 19:19:17Z cpesch $
 */

public class ReflectionHelper {
    /**
     * reflection factory for obtaining serialization constructors
     */
    private static ReflectionFactory reflectionFactory = (ReflectionFactory)
            AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());

    /**
     * To create objects without a constructor
     */
    public static Object newInstance(Class clazz)
            throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Class initCl = Object.class;
        Constructor cons = initCl.getDeclaredConstructor(new Class[0]);
        cons = reflectionFactory.newConstructorForSerialization(clazz, cons);
        cons.setAccessible(true);
        return cons.newInstance(new Object[0]);
    }

    /**
     * Returns private and superclass fields
     */
    public static Field getNonPublicField(Class clazz, String fieldName) throws NoSuchFieldException {
        Field field = getShadowedField(fieldName);
        if (field == null) {
            do {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                } catch (Exception e) {
                    throw new NoSuchFieldException("No such field '" + fieldName + "' in '" + clazz + "': " + e.getMessage());
                }
            } while (field == null && (clazz = clazz.getSuperclass()) != Object.class);
        }
        if (field == null)
            throw new NoSuchFieldException("No such field '" + fieldName + "' in '" + clazz + "'");
        return field;
    }

    /**
     * Returns shadowed fields
     */
    public static Field getShadowedField(String fieldName) throws NoSuchFieldException {
        int index = fieldName.lastIndexOf('.');
        if (index == -1)
            return null;
        String classQualifier = fieldName.substring(0, index);
        String fieldQualifier = fieldName.substring(index + 1);
        try {
            Class clazz = Class.forName(classQualifier);
            return clazz.getDeclaredField(fieldQualifier);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Returns private and superclass methods
     */
    public static Method getNonPublicMethod(Class clazz, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        Method method = getShadowedMethod(methodName, parameterTypes);
        if (method == null) {
            do {
                try {
                    method = clazz.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                } catch (Exception e) {
                    throw new NoSuchMethodException("No such method '" + methodName + "' in '" + clazz + "': " + e.getMessage());
                }
            } while (method == null && (clazz = clazz.getSuperclass()) != Object.class);
        }
        if (method == null)
            throw new NoSuchMethodException("No such method '" + methodName + "' in '" + clazz + "'");
        return method;
    }

    /**
     * Returns shadowed methods
     */
    public static Method getShadowedMethod(String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        int index = methodName.lastIndexOf('.');
        if (index == -1)
            return null;
        String classQualifier = methodName.substring(0, index);
        String methodQualifier = methodName.substring(index + 1);
        try {
            Class clazz = Class.forName(classQualifier);
            return clazz.getDeclaredMethod(methodQualifier, parameterTypes);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
