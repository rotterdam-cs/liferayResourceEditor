package com.aimprosoft.i18n.hook.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class WrapperUtils {

    public static Logger _logger = Logger.getLogger(WrapperUtils.class);

    public static List wrapCollection(Collection objects, Class wrapperClass) {
        return wrapCollection(objects, null, wrapperClass);
    }

    public static List wrapCollection(Collection objects, Class objectClass, Class wrapperClass) {
        return wrapCollection(objects, objectClass, wrapperClass, null);
    }

    @SuppressWarnings("unchecked")
    public static List wrapCollection(Collection objects, Class objectClass, Class wrapperClass, String field) {
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        List list = new LinkedList();

        for (Object object : objects) {
            final Object obj = wrapObject(object, objectClass, wrapperClass, field);
            if (obj != null) {
                list.add(obj);
            }
        }

        return list;
    }

    public static Object wrapObject(Object object, Class wrapperClass) {
        return wrapObject(object, object.getClass(), wrapperClass);
    }

    public static Object wrapObject(Object object, Class objectClass, Class wrapperClass) {
        return wrapObject(object, objectClass, wrapperClass, null);
    }

    public static Object wrapObject(Object object, Class objectClass, Class wrapperClass, String field) {
        try {
            return wrapperClass
                    .getConstructor(new Class[]{objectClass == null ? object.getClass() : objectClass})
                    .newInstance(field == null ? object : PropertyUtils.getProperty(object, field));
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            return null;
        }
    }

}