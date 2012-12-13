package com.liferay.portal.kernel.util;

import com.liferay.portal.model.PortletInfo;
import com.liferay.portlet.PortletResourceBundle;
import com.rcs.i18n.common.utils.RcsConstants;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageSourceUtil {

    public static String getMessage(ResourceBundle resourceBundle, String key) {

        String bundleName = RcsConstants.DEFAULT_BUNDLE_NAME;
        try {
            Field portletInfoField = PortletResourceBundle.class.getDeclaredField("_portletInfo");
            portletInfoField.setAccessible(true);
            PortletInfo portletInfo = (PortletInfo)portletInfoField.get(resourceBundle);
            bundleName = portletInfo.getTitle();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            if(key == null || resourceBundle == null){
                return  null;
            }
                Locale locale = null;
                try{
                    locale = resourceBundle.getLocale();
                }catch (Exception ignored){}
                if(locale == null){
                    locale  = Locale.US;
                }
                return getMessage(bundleName, locale, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMessage(Locale locale, String key) {
        return getMessage(RcsConstants.DEFAULT_BUNDLE_NAME, locale, key);
    }

    public static String getMessage(String bundleName, Locale locale, String key) {

        try {
            Object/*com.rcs.i18n.common.service.MessageSourceService*/ messageSourceService = ObjectFactory.getBean("messageSourceService");
            Object value = messageSourceService.getClass().getMethod("getMessage", String.class, String.class, Locale.class)
                    .invoke(messageSourceService, bundleName, key, locale);
            return (String) value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
