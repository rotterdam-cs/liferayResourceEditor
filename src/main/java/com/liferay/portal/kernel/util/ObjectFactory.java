package com.liferay.portal.kernel.util;

import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectFactory {

    private static Object getContext() {

        BeanLocatorImpl beanLocator = (BeanLocatorImpl)PortletBeanLocatorUtil.getBeanLocator("rcs");

        return beanLocator.getApplicationContext();
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
