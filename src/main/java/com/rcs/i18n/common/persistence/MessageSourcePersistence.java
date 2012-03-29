package com.rcs.i18n.common.persistence;

import com.rcs.i18n.common.model.impl.MessageSource;

import java.util.Collection;
import java.util.List;

public interface MessageSourcePersistence extends Persistence<MessageSource>{

    boolean isMessageSourceExist(String key, String locale);

    MessageSource getMessage(String key, String locale);

    List<MessageSource> findMessages(Collection keys);

    List<MessageSource> getMessageSourceList(int start, int end);

    List<MessageSource> findMessageSourceList(String key, String message, String locale, int start, int end);

    Integer findMessageSourceListCount(String key, String message, String locale);

    void updateThroughHQL(MessageSource messageSource);

    void deleteThroughHQL(String key);

    Integer selectMessageSourcesCount();
}
