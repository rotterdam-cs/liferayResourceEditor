package com.rcs.i18n.common.listener;

import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.rcs.i18n.common.cache.CacheService;
import com.rcs.i18n.common.config.ApplicationPropsBean;
import com.rcs.i18n.common.model.impl.MessageSource;
import com.rcs.i18n.common.persistence.MessageSourcePersistence;
import com.rcs.i18n.common.service.LocaleService;
import com.rcs.i18n.common.service.ObjectFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class HotDeployListenerHook implements HotDeployListener {

    private static final Logger _logger = Logger.getLogger(HotDeployListenerHook.class);

    public static final String DEFAULT_RESOURCE_NAME = "content/Language.properties";

    public static final String DEFAULT_RESOURCE_PREFIX = "content/Language_";
    public static final String DEFAULT_RESOURCE_SUFFIX = ".properties";

    private static LocaleService localeService = ObjectFactory.getBean(LocaleService.class);

    private static MessageSourcePersistence messageSourcePersistence = ObjectFactory.getBean(MessageSourcePersistence.class);

    private static ApplicationPropsBean props = ObjectFactory.getBean(ApplicationPropsBean.class);

    private static CacheService cacheService = ObjectFactory.getBean("ehCacheService");

    private static final ClassLoader CLASS_LOADER = messageSourcePersistence.getClass().getClassLoader();

    @Override
    public void invokeDeploy(HotDeployEvent hotDeployEvent) throws HotDeployException {

        Locale[] availableLocales = localeService.getAvailableLocales();

        Map<Locale,URL> resources = new HashMap<Locale, URL>();

        URL defaultResource = hotDeployEvent.getContextClassLoader().getResource(DEFAULT_RESOURCE_NAME);

        if (defaultResource != null){
            resources.put(Locale.getDefault(), defaultResource);
        }

        for (Locale locale : availableLocales) {

            String resourceName = DEFAULT_RESOURCE_PREFIX + locale.getLanguage() + DEFAULT_RESOURCE_SUFFIX;

            URL resource = hotDeployEvent.getContextClassLoader().getResource(resourceName);

            if (resource != null && !resources.containsKey(locale) && !resources.containsValue(resource)){
                resources.put(locale, resource);
            }
        }

        for (Locale locale : resources.keySet()) {

            URL resource = resources.get(locale);

            Properties bundle = new Properties();
            try {
                InputStream inStream = resource.openStream();
                bundle.load(inStream);
                inStream.close();
            } catch (IOException e) {
                _logger.warn("Could not read file", e);
                continue;
            }

            for (Map.Entry property : bundle.entrySet()) {

                String key = (String) property.getKey();
                String value = (String) property.getValue();

                MessageSource messageSource = messageSourcePersistence.getMessage(key, locale.toString());
                if (messageSource == null) {

                    messageSource = new MessageSource();
                    messageSource.setKey(key);
                    messageSource.setValue(value);
                    messageSource.setLocale(locale.toString());

                    ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
                    try {
                        Thread.currentThread().setContextClassLoader(CLASS_LOADER);
                        messageSourcePersistence.insert(messageSource);
                    } finally {
                        Thread.currentThread().setContextClassLoader(currentClassLoader);
                    }

                    //put into cache
                    if (props.isCacheEnabled()){
                        cacheService.putResult(MessageSource.class.getSimpleName(), null,
                                new Object[]{key, locale}, messageSource);
                    }
                }
            }

        }

    }

    @Override
    public void invokeUndeploy(HotDeployEvent event) throws HotDeployException {

    }
}
