/*
 * PrivateRooms is a bot managing vocal channels in a server
 * Copyright (C) 2022 RedsTom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.gravendev.privaterooms.configuration;

import jakarta.persistence.EntityManager;
import java.util.Objects;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;

@Configuration
public class DatabaseConfiguration {

    @Bean
    TransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    DataSource dataSource(BotConfiguration.ConfigModel config) {
        BasicDataSource ds = new BasicDataSource();

        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:" + config.dbHost() + ":" + config.dbPort() + "/" + config.dbName());
        ds.setUsername(config.dbUser());
        ds.setPassword(config.dbPassword());

        return ds;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);
        em.setJpaDialect(new HibernateJpaDialect());
        em.setPersistenceProvider(new HibernatePersistenceProvider());
        em.setPackagesToScan("org.gravendev.privaterooms", "org.gravendev.privaterooms.*");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    EntityManager entityManager(LocalContainerEntityManagerFactoryBean emf) {
        return Objects.requireNonNull(emf.getObject()).createEntityManager();
    }
}
