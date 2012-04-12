package com.rcs.i18n.hook.startup;

import com.rcs.i18n.common.cache.CacheService;
import com.rcs.i18n.common.config.ApplicationPropsBean;
import com.rcs.i18n.common.model.impl.MessageSource;
import com.rcs.i18n.common.persistence.MessageSourcePersistence;
import com.rcs.i18n.common.service.LocaleService;
import com.rcs.i18n.common.service.ObjectFactory;
import com.rcs.i18n.common.utils.RcsConstants;
import com.rcs.i18n.hook.util.PortalLanguageResourcesUtil;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class LanguageStartupProcessor extends SimpleAction {

    private static final Logger _logger = Logger.getLogger(LanguageStartupProcessor.class);

    private static ApplicationPropsBean props = ObjectFactory.getBean(ApplicationPropsBean.class);

    private static CacheService cacheService = ObjectFactory.getBean("ehCacheService");

    private static MessageSourcePersistence messageSourcePersistence = ObjectFactory.getBean(MessageSourcePersistence.class);

    private static LocaleService localeService = ObjectFactory.getBean(LocaleService.class);

    @Override
    public void run(String[] ids) throws ActionException {
        //if startup import is disabled
        if (!props.isImportOnStartup()){
            return;
        }

        _logger.info("Start language importing");

        try {
            _importBundle();
        } catch (Exception e){
            _logger.error("Import language bundle failed, due " + e.getMessage(), e);
        }

        _logger.info("Stop language importing");
    }

    private void _importBundle() throws Exception{

        Locale[] availableLocales = localeService.getAvailableLocales();
        
        _logger.info("Number of available locales is " + availableLocales.length);
        
        for (int i = 1; i <= availableLocales.length; i++){
            Locale locale = availableLocales[i - 1];

            _logger.info("Start importing locale " + locale.toString() + ", #" + i);

            Map<String, String> languageMap = getLanguageMap(locale);
            
            //go through language map
            for (Map.Entry<String, String> message: languageMap.entrySet()){
                //check if message isn't exist in the DB
                MessageSource messageSource = messageSourcePersistence.getMessage(message.getKey(), locale.toString());
                if (messageSource == null){
                    //otherwise put message into DB
                    messageSource = new MessageSource();
                    messageSource.setKey(message.getKey());
                    messageSource.setValue(message.getValue());
                    messageSource.setLocale(locale.toString());
                    messageSource.setBundle(RcsConstants.DEFAULT_BUNDLE_NAME);
                    messageSourcePersistence.insert(messageSource);
                }

                //put into cache
                if (props.isCacheEnabled()){
                    cacheService.putResult(MessageSource.class.getSimpleName(), null,
                            new Object[]{message.getKey(), locale}, messageSource);
                }
            }

            //small formatting
            int leftNum = availableLocales.length - i;
            String leftStr = leftNum == 0 ? "nothing" : String.valueOf(leftNum);

            _logger.info("Stop importing locale " + locale.toString() + ", " + leftStr + " left");
        }

    }

    private Map<String, String> getLanguageMap(Locale locale) {
        Map<String, String> languageMap;

        try {
            Locale invariantLocale = new Locale(locale.getLanguage());

            //put locale invariant to special map
            PortalLanguageResourcesUtil.putLanguageMap(invariantLocale);

            //get language map of parent locale
            languageMap = PortalLanguageResourcesUtil.getLanguageMap(invariantLocale);

            //put casual locale
            PortalLanguageResourcesUtil.putLanguageMap(locale);
            //get language map for casual locale and merge it with invariant locale
            languageMap.putAll(PortalLanguageResourcesUtil.getLanguageMap(locale));

            return languageMap;
        } catch (Exception e) {
            //come to the next in case of error
            _logger.error("Could not put new language map", e);

            return Collections.emptyMap();
        }
    }
}
