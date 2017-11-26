package com.teamtreehouse.giflib.dao;

import com.teamtreehouse.giflib.model.Gif;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class GifDaoImpl implements GifDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Gif> findAll() {
        Session session = sessionFactory.openSession();

        // LONG FORM
        // Get a CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Get a CriteriaQuery for the Category class
        CriteriaQuery<Gif> criteriaQuery = builder.createQuery(Gif.class);

        // Specify Criteria root
        criteriaQuery.from(Gif.class);

        // Execute query
        List<Gif> gifs = session.createQuery(criteriaQuery).getResultList();

        // SHORT FORM ATTEMPT - which doesn't work (cast exception)...need to troubleshoot
//        List<Gif> gifs = session.createQuery(
//                (CriteriaQuery<Gif>)session.getCriteriaBuilder().createQuery(Gif.class).from(Gif.class)
//        ).getResultList();

        session.close();
        return gifs;
    }

    @Override
    public Gif findById(Long id) {
        Session session = sessionFactory.openSession();
        Gif gif = session.get(Gif.class, id);
        session.close();
        return gif;
    }

    @Override
    public void save(Gif gif) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(gif);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Gif gif) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(gif);
        session.getTransaction().commit();
        session.close();
    }
}
