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

public class LanguageStartupProcessor extends BaseStartupProcessor {


    @Override
    public void run(String[] ids) throws ActionException {

        _logger.info("Registering custom hot deploy listener");

        //if startup import is disabled
        if (!props.isImportOnStartup()) {
            return;
        }

        _logger.info("Start language importing");

        try {
            _importBundle(RcsConstants.DEFAULT_BUNDLE_NAME);
        } catch (Exception e) {
            _logger.error("Import language bundle failed, due " + e.getMessage(), e);
        }

        _logger.info("Stop language importing");
    }

    protected Map<String, String> getLanguageMap(Locale locale) {
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
