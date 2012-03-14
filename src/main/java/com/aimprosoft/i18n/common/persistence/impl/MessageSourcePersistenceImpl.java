package com.aimprosoft.i18n.common.persistence.impl;

import com.aimprosoft.i18n.common.model.impl.MessageSource;
import com.aimprosoft.i18n.common.persistence.MessageSourcePersistence;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
                Query query = session.createQuery("select ms from MessageSource ms where ms.key = :key and ms.locale = :locale");

                query.setParameter("key", key);
                query.setParameter("locale", locale);

                return (MessageSource) query.uniqueResult();
            }
        });
    }

    @Override
    public List<MessageSource> getMessageSourceList(final int start, final int end) {
        return getHibernateTemplate().execute(new HibernateCallback<List<MessageSource>>() {
            @Override
            public List<MessageSource> doInHibernate(Session session) throws HibernateException, SQLException {

                Query sql = session.createSQLQuery
                        ("select ms.* from (select distinct(resourcekey) resourcekey from messagesource limit :limit offset :offset) qe," +
                                " messagesource ms where ms.resourcekey = qe.resourcekey order by ms.resourcekey;")
                        .addEntity("ms", MessageSource.class);
                sql.setInteger("limit", end - start)
                    .setInteger("offset", start);
                return sql.list();

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
