//package com.lays.repository;
//
//import com.lays.model.User;
//import org.hibernate.SessionFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class UserRepositoryHibernate {
//
//    private final SessionFactory sessionFactory;
//
//    public UserRepositoryHibernate(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public List<User> findAll() {
//        return sessionFactory.getCurrentSession()
//                .createQuery("from User", User.class)
//                .list();
//    }
//
//    public User findById(Long id) {
//        return sessionFactory.getCurrentSession()
//                .get(User.class, id);
//    }
//
//    public User save(User user) {
//        sessionFactory.getCurrentSession().saveOrUpdate(user);
//        return user;
//    }
//
//    public void delete(Long id) {
//        User user = findById(id);
//        if (user != null) {
//            sessionFactory.getCurrentSession().delete(user);
//        }
//    }
//}