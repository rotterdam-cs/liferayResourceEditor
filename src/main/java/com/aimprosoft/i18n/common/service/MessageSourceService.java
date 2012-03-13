package com.aimprosoft.i18n.common.service;

import com.aimprosoft.i18n.common.message.CustomMessage;
import com.aimprosoft.i18n.common.model.impl.MessageSource;

import java.util.List;
import java.util.Locale;

public interface MessageSourceService {
    
    String getMessage(String key);
    
    String getMessage(String key, Locale locale);
    
    String getMessage(String key, Locale locale, String defaultValue);
    
    String getMSWJson(int start, int end);

    String getMessageSourceWrappers(int start, int end);

    List<CustomMessage> saveMessageSources(String data, Boolean save);

    List<CustomMessage> delete(String data);

    Integer getMessageSourcesCount();

}
