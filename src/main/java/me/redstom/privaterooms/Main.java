package me.redstom.privaterooms;

import me.redstom.privaterooms.util.Config;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"me.redstom.privaterooms.*", "me.redstom.privaterooms"})
@EnableJpaRepositories("me.redstom.privaterooms.*")
public class Main {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Main.class.getPackageName());

        PrivateRooms bot = ctx.getBean(PrivateRooms.class);
        bot.init();
        bot.run();

    }

    @Bean
    public Config config(PrivateRooms privateRooms) {
        return privateRooms.config();
    }

    @Bean
    public TransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public DataSource dataSource(PrivateRooms pr) {
        if(!pr.initialized()) {
            pr.init();
        }

        Config config = pr.config();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + config.database().host() + ":" + config.database().port() + "/" + config.database().database());
        dataSource.setUsername(config.database().username());
        dataSource.setPassword(config.database().password());

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);
        em.setJpaDialect(new HibernateJpaDialect());
        em.setPersistenceProvider(new HibernatePersistenceProvider());
        em.setPackagesToScan("me.redstom.privaterooms.*", "me.redstom.privaterooms");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);

        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    public EntityManager entityManager(LocalContainerEntityManagerFactoryBean emf) {
        return emf.getObject().createEntityManager();
    }
}
