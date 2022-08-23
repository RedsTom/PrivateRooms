package me.redstom.privaterooms;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.SneakyThrows;
import me.redstom.privaterooms.util.Config;
import me.redstom.privaterooms.util.command.ICommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import javax.persistence.EntityManager;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"me.redstom.privaterooms.*", "me.redstom.privaterooms"})
@EnableJpaRepositories("me.redstom.privaterooms.*")
public class Main {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Main.class);

        PrivateRooms bot = ctx.getBean(PrivateRooms.class);
        bot.run();
    }

    @Bean
    @SneakyThrows
    public Config config() {
        File configFile = new File("config.toml");

        if (!configFile.exists()) {
            configFile.createNewFile();

            Config config = new Config(
              "TOKEN HERE",
              new Config.DatabaseConfig(
                "localhost",
                5432,
                "postgres",
                "postgres",
                "postgres"
              )
            );

            TomlWriter writer = new TomlWriter();
            writer.write(config, configFile);

            System.err.println("Config file created at " + configFile.getAbsolutePath());
            System.err.println("Please fill in the config file and restart the bot");

            System.exit(1);
        }

        Toml cfg = new Toml();
        cfg.read(configFile);

        return cfg.to(Config.class);
    }

    @Lazy
    @Bean
    @SneakyThrows
    public JDA client(Config config) {
        return JDABuilder
          .createDefault(config.token())
          .enableIntents(EnumSet.allOf(GatewayIntent.class))
          .build();
    }

    @Bean
    public TransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public DataSource dataSource(Config config) {
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
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);

        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    public EntityManager entityManager(LocalContainerEntityManagerFactoryBean emf) {
        return emf.getObject().createEntityManager();
    }

    @Bean
    public List<ICommand> commands() {
        return new ArrayList<>();
    }
}
