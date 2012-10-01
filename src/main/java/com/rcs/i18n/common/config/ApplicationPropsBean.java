package com.rcs.i18n.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("props")
public class ApplicationPropsBean {

    @Value("${application.cache.enabled}")
    private boolean cacheEnabled;
    
    @Value("${import.on.startup}")
    private boolean importOnStartup;

    @Value("${import.bundles.on.startup}")
    private boolean importBundlesOnStartup;

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public boolean isImportOnStartup() {
        return importOnStartup;
    }

    public boolean isImportBundlesOnStartup() {
        return importBundlesOnStartup;
    }

}
