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
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalService;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.rcs.i18n.common.listener.HotDeployListenerHook;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.apache.log4j.Logger;

import java.util.logging.Level;
import javax.servlet.ServletContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.portlet.util.PortletUtils;

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
        
        /*
        List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();
        _logger.info("portlet iteration");
        
        
        for (Portlet portlet : portlets) {
            if (StringUtils.isNotBlank(portlet.getContextPath())) {                
                String portletId = portlet.getPortletId();
                com.liferay.portal.kernel.portlet.PortletBag portletBag = com.liferay.portal.kernel.portlet.PortletBagPool.get(portletId);
                ServletContext servletContext = portletBag.getServletContext();
                
                
                URL resource = null;
                try {
                    resource = servletContext.getResource("/WEB-INF/classes/content/Language.properties");
                } catch (MalformedURLException ex) {
                    java.util.logging.Logger.getLogger(LanguageStartupProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                if (resource != null) {
                    Properties bundle = new Properties();
                    try {
                        InputStream inStream = resource.openStream();
                        bundle.load(inStream);
                        inStream.close();
                        for (Map.Entry<String, String> message : (new HashMap<String, String>((Map) bundle)).entrySet()) {
                            //check if message isn't exist in the DB
                            _logger.warn("info: " + message.getKey());
                        }
                        //return new HashMap<String, String>((Map) bundle 

                    } catch (IOException e) {
                        _logger.warn("Could not read file", e);
                    }
                }
            }
        }    */    
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
