package com.aimprosoft.i18n.hook.util;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;

public final class PortalReflectionUtil {

    private static final Logger _logger = Logger.getLogger(PortalReflectionUtil.class);
    
    public static ClassLoader getPortalClassLoader(){
        return PortalClassLoaderUtil.getClassLoader();
    }
    
    public static Class getPortalClass(String className){
        try {
            return getPortalClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            _logger.warn("Could not load class " + className);
            return null;
        }
    }
    
    public static Field getPortalField(String className, String fieldName) {
        try {
            return getPortalClass(className).getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            _logger.warn("Could not find field " + fieldName + " in class " + className);
            return null;
        }
    }
    public static Object getPortalFieldValue(String className, String fieldName) {
        try {
            Field field = getPortalField(className, fieldName);

            if (field == null){
                return null;
            }

            //make sure this is accessible
            if (!field.isAccessible()){
                field.setAccessible(true);
            }

            return field.get(null);
        } catch (Exception e) {
            _logger.warn("Could not get value in field " + fieldName + " in class " + className);
            return null;        }

    }
}
