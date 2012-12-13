package com.rcs.i18n.hook.startup;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.util.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class LoadCurrentBundlesProcessor extends BaseStartupProcessor {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private String baseResourceName = "content/Language";

    public static final String DEFAULT_RESOURCE_SUFFIX = ".properties";

    @Override
    public void run(String[] ids) throws ActionException {

        if (!props.isImportResourceEditorBundles()) {
            return;
        }

        _logger.info("Start language importing from \"Resource Editor\"");

        try {
            _importBundle("Resource Editor");
        } catch (Exception e) {
            _logger.error("Import language bundle failed, due " + e.getMessage(), e);
        }


    }

    protected Map<String, String> getLanguageMap(Locale locale) {

        String resourceName = baseResourceName + StringPool.UNDERLINE + locale.getLanguage() + DEFAULT_RESOURCE_SUFFIX;
        
        URL resource = classLoader.getResource(resourceName);

        if (resource == null) {
            resourceName = baseResourceName + StringPool.UNDERLINE + locale.toString() + DEFAULT_RESOURCE_SUFFIX;
            resource = classLoader.getResource(resourceName);
        }

        if (resource == null) {
            resourceName = baseResourceName + StringPool.UNDERLINE + DEFAULT_RESOURCE_SUFFIX;
            resource = classLoader.getResource(resourceName);
        }

        if (resource != null) {

            Properties bundle = new Properties();

            try {

                InputStream inStream = resource.openStream();
                bundle.load(inStream);
                inStream.close();

                return new HashMap<String, String>((Map) bundle);

            } catch (IOException e) {
                _logger.warn("Could not read file", e);
            }
        }

        return Collections.emptyMap();

    }


}
