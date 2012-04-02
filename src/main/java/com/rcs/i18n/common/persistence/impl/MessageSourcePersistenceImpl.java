package com.rcs.i18n.common.persistence.impl;

import com.rcs.i18n.common.model.impl.MessageSource;
import com.rcs.i18n.common.persistence.MessageSourcePersistence;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MessageSourcePersistenceImpl extends PersistenceImpl<MessageSource> implements MessageSourcePersistence{
    @Override
    public boolean isMessageSourceExist(final String key, final String locale) {
        return getHibernateTemplate().execute(new HibernateCallback<Boolean>() {
            @Override
            public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery("select count(ms) from MessageSource ms where ms.key = :key and ms.locale = :locale");

                query.setParameter("key", key);
                query.setParameter("locale", locale);

                return ((Number) query.uniqueResult()).intValue() > 0;
            }
        });
    }

    @Override
    public MessageSource getMessage(final String key, final String locale) {

        return getHibernateTemplate().execute(new HibernateCallback<MessageSource>() {
            @Override
            public MessageSource doInHibernate(Session session) throws HibernateException, SQLException {
                String hqlQueryString = "select ms from MessageSource ms where key=:key and locale=:locale";
                Query hqlQuery = session.createQuery(hqlQueryString);
                hqlQuery.setParameter("key", key);
                hqlQuery.setParameter("locale", locale);
                List<MessageSource> resultList = hqlQuery.list();
                if (resultList != null && resultList.size() > 0) {
                    return resultList.get(0);
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public List<MessageSource> findMessages(final Collection keys) {

        return getHibernateTemplate().execute(new HibernateCallback<List<MessageSource>>() {
            @Override
            public List<MessageSource> doInHibernate(Session session) throws HibernateException, SQLException {
                String hqlQueryString = "select ms from MessageSource ms where key in :keys order by key";
                Query hqlQuery = session.createQuery(hqlQueryString);
                hqlQuery.setParameterList("keys", keys);
                return hqlQuery.list();
            }
        });
    }

    @Override
    public List<MessageSource> getMessageSourceList(final int start, final int end) {
        return getHibernateTemplate().execute(new HibernateCallback<List<MessageSource>>() {
            @Override
            public List<MessageSource> doInHibernate(Session session) throws HibernateException, SQLException {

                String hqlQueryString = "select distinct(m.key) from MessageSource m";
                Query hqlQuery = session.createQuery(hqlQueryString);
                hqlQuery.setFirstResult(start);
                hqlQuery.setMaxResults(end - start);
                List<String> keys = hqlQuery.list();

                if (keys != null && keys.size() > 0) {
                    return findMessages(keys);
                } else {
                    return new ArrayList<MessageSource>();
                }

            }
        });
    }

    @Override
    public List<MessageSource> findMessageSourceList(final String key, final String value, final String locale, final int start, final int end) {
        return getHibernateTemplate().execute(new HibernateCallback<List<MessageSource>>() {
            @Override
            public List<MessageSource> doInHibernate(Session session) throws HibernateException, SQLException {

                String searchKey = key + "%";
                String searchValue = value + "%";
                String hqlQuery = "select distinct(m.key) from MessageSource m where ";
                if (StringUtils.isBlank(value)) {
                    hqlQuery += "m.key like :key";
                } else if (StringUtils.isBlank(key)) {
                    hqlQuery += "m.value like :value and m.locale = :locale";
                } else {
                    hqlQuery += "m.key like :key or (m.value like :value and m.locale = :locale)";
                }
                hqlQuery += " order by m.key";

                Query query = session.createQuery(hqlQuery);
                if (hqlQuery.contains(":key"))
                    query.setString("key", searchKey);
                if (hqlQuery.contains(":value"))
                    query.setString("value", searchValue);
                if (hqlQuery.contains(":locale"))
                    query.setString("locale", locale);

                query.setFirstResult(start);
                query.setMaxResults(end - start);
                List<String> keys = query.list();

                if (keys != null && keys.size() > 0) {
                    return findMessages(keys);
                } else {
                    return new ArrayList<MessageSource>();
                }
            }
        });
    }

    @Override
    public void updateThroughHQL(final MessageSource messageSource) {

        //this is workaround of PostgreSQL DB
        //By default Hibernate for @Lob in PortgreSQL creates separate table with id in appropriate table
        //That's why we can't be sure whether @Lob is inlined into table or not
        MessageSource messageSourceDB = getHibernateTemplate().execute(new HibernateCallback<MessageSource>() {
            @Override
            public MessageSource doInHibernate(Session session) throws HibernateException, SQLException {
                Query hql = session.createQuery("select ms from MessageSource ms where  ms.key = :key and  ms.locale = :locale");

                hql.setString("key", messageSource.getKey());
                hql.setString("locale", messageSource.getLocale());

                return (MessageSource)hql.uniqueResult();
            }
        });

        messageSourceDB.setValue(messageSource.getValue());

        update(messageSourceDB);

    }

    @Override
    public void deleteThroughHQL(final String key) {
        getHibernateTemplate().execute(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query hql = session.createQuery("delete from MessageSource ms where ms.key = :key");

                hql.setString("key", key);

                hql.executeUpdate();

                return null;

            }
        });
    }

    @Override
    public Integer findMessageSourceListCount(final String key, final String value, final String locale) {
        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {

                String hqlQuery;
                if (StringUtils.isBlank(value)) {
                    hqlQuery = "select distinct(ms.key) from MessageSource ms where ms.key like '" + key + "%'";
                } else if (StringUtils.isBlank(key)) {
                    hqlQuery = "select distinct(ms.key) from MessageSource ms where ms.value like '" + value + "%' and ms.locale = '" + locale + "'";
                } else {
                    hqlQuery = "select distinct(ms.key) from MessageSource ms where ms.key like '" + key + "%' or (ms.value like '" + value + "%' and ms.locale = '" + locale + "')";
                }

                Query query = session.createQuery(hqlQuery);
                List resultList = null;
                try{
                    resultList = query.list();
                } catch (Exception e) {
                    resultList = new ArrayList();
                }
                return resultList.size();
            }
        });
    }

    @Override
    public Integer selectMessageSourcesCount() {
        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            @Override
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Query hql = session.createQuery("select count(distinct ms.key) from MessageSource ms");
                return ((Long)hql.uniqueResult()).intValue();
            }
        });
    }
}
