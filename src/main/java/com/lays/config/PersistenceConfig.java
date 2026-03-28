//package com.lays.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import jakarta.persistence.EntityManagerFactory;
//import org.hibernate.SessionFactory;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.hibernate5.HibernateTransactionManager;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
//import org.springframework.orm.jpa.vendor.Database;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
//@Configuration
//@EnableTransactionManagement
//@PropertySource("classpath:persistence.properties")
//@EnableJpaRepositories("com.lays.repository")
//public class PersistenceConfig implements EnvironmentAware {
//
//    private Environment environment;
//
//    @Override
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
//        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
//        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
//        dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
//        return dataSource;
//    }
//
//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
//        entityManagerFactoryBean.setPackagesToScan("com.lays.model");
//        entityManagerFactoryBean.setDataSource(dataSource());
//        entityManagerFactoryBean.afterPropertiesSet();
//        return entityManagerFactoryBean.getObject();
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory());
//        return transactionManager;
//    }
//
//    @Bean
//    public PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }
//
//    @Bean
//    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
//        return new PersistenceAnnotationBeanPostProcessor();
//    }
//
//    @Bean
//    public HibernateJpaVendorAdapter jpaVendorAdapter() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setDatabase(Database.valueOf(environment.getProperty("spring.database")));
//        vendorAdapter.setShowSql(true);
//        vendorAdapter.setGenerateDdl(true);
//        return vendorAdapter;
//    }
//
////    @Bean
////    public LocalSessionFactoryBean sessionFactory() {
////        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
////        sessionFactory.setDataSource(dataSource());
////        sessionFactory.setPackagesToScan("com.lays.model");
////        Properties hibernateProperties = new Properties();
////        hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
////        sessionFactory.setHibernateProperties(hibernateProperties);
////        return sessionFactory;
////    }
//
////    @Bean
////    public PlatformTransactionManager hibernateTransactionManager(LocalSessionFactoryBean sessionFactory) {
////        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
////        transactionManager.setSessionFactory(sessionFactory().getObject());
////        return new HibernateTransactionManager(sessionFactory.getObject());
////    }
//}