package com.aimprosoft.i18n.common.service.impl;

import com.aimprosoft.i18n.common.cache.CacheService;
import com.aimprosoft.i18n.common.config.ApplicationPropsBean;
import com.aimprosoft.i18n.common.message.CustomMessage;
import com.aimprosoft.i18n.common.model.impl.MessageSource;
import com.aimprosoft.i18n.common.persistence.MessageSourcePersistence;
import com.aimprosoft.i18n.common.service.MessageSourceService;
import com.aimprosoft.i18n.hook.model.MessageSourceWrapper;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Service("messageSourceService")
public class MessageSourceServiceImpl implements MessageSourceService {

    private Logger _logger = Logger.getLogger(getClass());

    @Autowired
    private MessageSourcePersistence persistence;

    @Autowired
    private ApplicationPropsBean props;

    @Autowired
    @Qualifier(value = "ehCacheService")
    private CacheService cacheService;

    @Override
    public String getMessage(String key) {
        return getMessage(key, getDefaultLocale(), key);
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return getMessage(key, locale, key);
    }

    @Override
    public String getMessage(String key, Locale locale, String defaultValue) {
        try {
            MessageSource message = persistence.getMessage(key, locale.toString());
            return message == null ? defaultValue : message.getValue();
        } catch (Exception e) {
            _logger.warn("Couldn't get message with key '" + key + "', returning default", e);
            return defaultValue;
        }
    }

    @Override
    public String getMSWJson(int start, int end) {
        return new StringBuilder()
                .append("{\"totalRecords\":\"")
                .append(getMessageSourcesCount())
                .append("\",")
                .append("\"start\":\"")
                .append(start)
                .append("\",")
                .append("\"records\":")
                .append(getMessageSourceWrappers(start, end))
                .append("}")
                .toString();
    }

    @Override
    public String getMessageSourceWrappers(int start, int end) {
        try {
            List<MessageSource> messageSourceList = persistence.getMessageSourceList(start, end);

            List<MessageSourceWrapper> messageSourceWrapperList = wrapMessageSources(messageSourceList);

            return getMSWrappersJSON(messageSourceWrapperList);
        } catch (Exception e) {
            _logger.error("Cannot get resource cause: " + e.getMessage(), e);
        }
        return "[]";
    }

    @Override
    public List<CustomMessage> saveMessageSources(String data, Boolean save) {

        try {
            List<MessageSource> messageSourceList = deserializeMessageSources(data);

            for (MessageSource messageSource : messageSourceList) {

                if (save) {
                    persistence.insert(messageSource);
                } else {
                    persistence.updateThroughHQL(messageSource);
                }

                //put into cache
                if (props.isCacheEnabled()) {
                    cacheService.putResult(MessageSource.class.getSimpleName(), null,
                            new Object[]{messageSource.getKey(), messageSource.getLocale()}, messageSource);
                }
            }
        } catch (IOException e) {
            _logger.error("Cannot save data cause: " + e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private List<MessageSource> deserializeMessageSources(String data) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(data, new TypeReference<List<MessageSource>>() {});
    }

    @Override
    public List<CustomMessage> delete(String data) {
        if (StringUtils.isNotBlank(data)) {
            persistence.deleteThroughHQL(data);
        }
        return Collections.emptyList();
    }

    @Override
    public Integer getMessageSourcesCount() {
        return persistence.selectMessageSourcesCount();
    }

    private String getMSWrappersJSON(List<MessageSourceWrapper> messageSourceWrapperList) {
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            om.writeValue(sw, messageSourceWrapperList);
        } catch (IOException ignored) {
        }
        return sw.toString();
    }

    private List<MessageSourceWrapper> wrapMessageSources(List<MessageSource> messageSourceList) {

        List<MessageSourceWrapper> messageSourceWrapperList = new LinkedList<MessageSourceWrapper>();

        MessageSourceWrapper messageSourceWrapper = null;// = new MessageSourceWrapper();
        String preKey = null;
        for (MessageSource messageSource : messageSourceList) {
            if (!StringUtils.equals(preKey, messageSource.getKey())) {
                messageSourceWrapper = new MessageSourceWrapper();
                messageSourceWrapper.setKey(messageSource.getKey());
                messageSourceWrapper.setSource(new HashMap<String, String>());
                messageSourceWrapperList.add(messageSourceWrapper);
            }
            messageSourceWrapper.getSource().put(messageSource.getLocale(), messageSource.getValue());
            preKey = messageSource.getKey();

        }
        return messageSourceWrapperList;
    }

    private Locale getDefaultLocale() {
        //get default locale as default locale of request
        Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

        //get default locale as default locale of portal
        if (locale == null) {
            locale = LocaleThreadLocal.getDefaultLocale();
        }

        //get default locale as default locale of server
        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;

    }
}
