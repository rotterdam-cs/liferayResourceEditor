package com.aimprosoft.i18n.common.model.impl;

import com.aimprosoft.i18n.common.model.BaseModel;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity
@Table(name = "MessageSource")
public class MessageSource extends BaseModel {
    
    @Column(name = "resourceKey", nullable = false)
    @Index(name = "resourcekey_index")
    private String key;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "resourceValue", nullable = false)
    private String value;

    @Column(name = "resouerceLocale", nullable = false)
    private String locale;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
