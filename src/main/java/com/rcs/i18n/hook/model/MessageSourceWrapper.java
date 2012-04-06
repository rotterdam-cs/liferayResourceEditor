package com.rcs.i18n.hook.model;

import java.util.Map;

public class MessageSourceWrapper {

    private String key;
    private String bundle;
    private Map<String, String> source;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }
}
