package com.rcs.i18n.common.listener;

import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.Portal;
import com.rcs.i18n.common.cache.CacheService;
import com.rcs.i18n.common.config.ApplicationPropsBean;
import com.rcs.i18n.common.model.impl.MessageSource;
import com.rcs.i18n.common.persistence.MessageSourcePersistence;
import com.rcs.i18n.common.service.LocaleService;
import com.rcs.i18n.common.service.ObjectFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

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

        ServletContext servletContext = hotDeployEvent.getServletContext();

        ClassLoader contextClassLoader = hotDeployEvent.getContextClassLoader();

        String portletXML = StringUtils.EMPTY;
        try {
            portletXML = HttpUtil.URLtoString(servletContext.getResource("/WEB-INF/" + Portal.PORTLET_XML_FILE_NAME_STANDARD));
        } catch (IOException e) {
            _logger.error("Can not read portlet.xml");
        }

        if (StringUtils.isNotBlank(portletXML)) {

            try {
                readPortletXML(portletXML, contextClassLoader);
            } catch (DocumentException e) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Unable to read process xml. ");
                }
            }

        }
    }

    /*
    *  Reads 'resource-bundle' property from portlet.xml
    * */
    private void readPortletXML(String xml, ClassLoader classLoader) throws DocumentException {

        Document document = SAXReaderUtil.read(xml, true);

        Element rootElement = document.getRootElement();

        Locale[] availableLocales = localeService.getAvailableLocales();

        for (Element portletElement : rootElement.elements("portlet")) {

            String resourceBundleName = portletElement.elementText("resource-bundle");

            String defaultResourceName = resourceBundleName + DEFAULT_RESOURCE_SUFFIX;

            URL defaultResource = classLoader.getResource(defaultResourceName);

            if (defaultResource != null) {

                Properties bundle = new Properties();
                try {
                    InputStream inStream = defaultResource.openStream();
                    bundle.load(inStream);
                    inStream.close();
                } catch (IOException e) {
                    _logger.warn("Could not read file", e);
                }

                processBundle(Locale.getDefault(), bundle);
            }

            for (Locale locale : availableLocales) {

                String resourceName = resourceBundleName + StringPool.UNDERLINE + locale.getLanguage() + DEFAULT_RESOURCE_SUFFIX;

                URL resource = classLoader.getResource(resourceName);

                if (resource != null) {

                    Properties bundle = new Properties();
                    try {
                        InputStream inStream = resource.openStream();
                        bundle.load(inStream);
                        inStream.close();
                    } catch (IOException e) {
                        _logger.warn("Could not read file", e);
                        continue;
                    }

                    processBundle(locale, bundle);
                }
            }
        }
    }

    /*
    *   Processed resource bundle for specified locale
    * */
    private void  processBundle(Locale locale, Properties bundle) {

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
                if (props.isCacheEnabled()) {
                    cacheService.putResult(MessageSource.class.getSimpleName(), null,
                            new Object[]{key, locale}, messageSource);
                }
            }
        }

    }

    @Override
    public void invokeUndeploy(HotDeployEvent event) throws HotDeployException {

    }
}
