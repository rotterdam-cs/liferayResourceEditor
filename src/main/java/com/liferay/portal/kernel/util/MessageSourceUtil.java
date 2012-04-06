package com.liferay.portal.kernel.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageSourceUtil {

    public static String getMessage(ResourceBundle resourceBundle, String key) {

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
                return getMessage(locale, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMessage(Locale locale, String key) {

        try {
            Object/*com.rcs.i18n.common.service.MessageSourceService*/ messageSourceService = ObjectFactory.getBean("messageSourceService");
            Object value = messageSourceService.getClass().getMethod("getMessage", String.class, Locale.class).invoke(messageSourceService, key, locale);
            return (String) value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
