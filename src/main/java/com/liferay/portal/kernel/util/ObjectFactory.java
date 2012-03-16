package com.liferay.portal.kernel.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectFactory {

    public static Object/*org.springframework.context.ApplicationContext*/ applicationContext;

    private static Object getContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Object applicationContext) {
        ObjectFactory.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return getBean(getBeanNameMethod(), beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class className) {
        try {
            return (T) getBean(getBeanClassMethod(), className);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getBean(Method method, Object param) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(getContext(), param);
    }

    private static Method getBeanNameMethod() throws NoSuchMethodException {
        return getContext().getClass().getMethod("getBean", new Class[]{String.class});
    }

    private static Method getBeanClassMethod() throws NoSuchMethodException {
        return getContext().getClass().getMethod("getBean", new Class[]{Class.class});
    }

}
