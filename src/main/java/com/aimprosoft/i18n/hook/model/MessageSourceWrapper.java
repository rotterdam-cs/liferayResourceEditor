package com.aimprosoft.i18n.hook.model;

import java.util.List;
import java.util.Map;

public class MessageSourceWrapper {

    private String key;
    private Map<String, String> source;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }
}
