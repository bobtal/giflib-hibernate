package com.teamtreehouse.giflib.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public abstract class HibernateDao<T> {

    @Autowired
    SessionFactory sessionFactory;

    Class<T> typeParameterClass;

    public List<T> findAll() {
        Session session = sessionFactory.openSession();

        // LONG FORM
        // Get a CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Get a CriteriaQuery for the Category class
        CriteriaQuery<T> criteriaQuery = builder.createQuery(typeParameterClass);

        // Specify Criteria root
        criteriaQuery.from(typeParameterClass);

        // Execute query
        List<T> ts = session.createQuery(criteriaQuery).getResultList();

        // SHORT FORM ATTEMPT - which doesn't work (cast exception)...need to troubleshoot
//        List<T> ts = session.createQuery(
//                (CriteriaQuery<Gif>)session.getCriteriaBuilder().createQuery(typeParameterClass).from(typeParameterClass)
//        ).getResultList();

        session.close();
        return ts;
    }

    public void save(T t){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(t);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(T t) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(t);
        session.getTransaction().commit();
        session.close();
    }
}
