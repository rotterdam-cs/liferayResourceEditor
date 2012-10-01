package com.rcs.i18n.hook.startup;

import com.liferay.portal.kernel.events.SimpleAction;
import com.rcs.i18n.common.cache.CacheService;
import com.rcs.i18n.common.config.ApplicationPropsBean;
import com.rcs.i18n.common.model.impl.MessageSource;
import com.rcs.i18n.common.persistence.MessageSourcePersistence;
import com.rcs.i18n.common.service.LocaleService;
import com.rcs.i18n.common.service.ObjectFactory;
import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.Map;

public abstract class BaseStartupProcessor extends SimpleAction {

    protected static final Logger _logger = Logger.getLogger(LoadPortletsBundlesProcessor.class);

    protected static ApplicationPropsBean props = ObjectFactory.getBean(ApplicationPropsBean.class);

    protected static CacheService cacheService = ObjectFactory.getBean("ehCacheService");

    protected static MessageSourcePersistence messageSourcePersistence = ObjectFactory.getBean(MessageSourcePersistence.class);

    protected static LocaleService localeService = ObjectFactory.getBean(LocaleService.class);


    protected void checkAndSaveLanguageMap(Map<String, String> languageMap, Locale locale, String bundleName) {

        //go through language map
        for (Map.Entry<String, String> message : languageMap.entrySet()) {
            //check if message isn't exist in the DB
            MessageSource messageSource = messageSourcePersistence.getMessage(message.getKey(), locale.toString());
            if (messageSource == null) {
                //otherwise put message into DB
                messageSource = new MessageSource();
                messageSource.setKey(message.getKey());
                messageSource.setValue(message.getValue());
                messageSource.setLocale(locale.toString());
                messageSource.setBundle(bundleName);
                messageSourcePersistence.insert(messageSource);
            }

            //put into cache
            if (props.isCacheEnabled()) {
                cacheService.putResult(MessageSource.class.getSimpleName(), null,
                        new Object[]{message.getKey(), locale}, messageSource);
            }
        }

    }

    protected void _importBundle(String bundleName) throws Exception {

        Locale[] availableLocales = localeService.getAvailableLocales();

        _logger.info("Number of available locales is " + availableLocales.length);

        for (int i = 1; i <= availableLocales.length; i++) {

            Locale locale = availableLocales[i - 1];

            _logger.info("Start importing locale " + locale.toString() + ", for bundle " + "\"" + bundleName + "\"" + ", #" + i);

            Map<String, String> languageMap = getLanguageMap(locale);

            checkAndSaveLanguageMap(languageMap, locale, bundleName);

            //small formatting
            int leftNum = availableLocales.length - i;
            String leftStr = leftNum == 0 ? "nothing" : String.valueOf(leftNum);

            _logger.info("Stop importing locale " + locale.toString() + ", " + leftStr + " left");
        }

    }

    protected abstract Map<String, String>  getLanguageMap(Locale locale);

}
