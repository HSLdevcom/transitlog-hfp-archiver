package fi.hsl;

import com.zaxxer.hikari.HikariDataSource;
import fi.hsl.common.BlobStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.PodamUtils;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.TypeManufacturer;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;

import static org.mockito.Mockito.mock;

@Configuration
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(
        basePackages = "fi.hsl.domain",
        entityManagerFactoryRef = "entityManagerFactoryBean",
        transactionManagerRef = "platformTransactionManager"
)
public class EnableBatchTestConfiguration {
    @Autowired
    private Environment env;


    @Bean
    @Primary
    public BlobStorage blobStorage() {
        return mock(BlobStorage.class);
    }

    @Bean
    public PodamFactory testPodamFactory() {

        TypeManufacturer<Integer> manufacturer = new IntTypeManufacturerImpl() {

            @Override
            public Integer getInteger(AttributeMetadata attributeMetadata) {
                if (attributeMetadata.getPojoClass() == Timestamp.class) {
                    return PodamUtils.getIntegerInRange(0, 999999999);
                } else {
                    return super.getInteger(attributeMetadata);
                }
            }
        };
        PodamFactoryImpl podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(int.class, manufacturer);
        return podamFactory;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactoryBean().getObject());
        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(
                "fi.hsl.domain");
        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.jdbc.time_zone", "UTC");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public DataSource dataSource() {

        HikariDataSource dataSource
                = new HikariDataSource();
        dataSource.setDriverClassName(
                env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        Properties dsProperties = new Properties();
        dsProperties.put("connectionTimeout", 0);
        dsProperties.put("idleTimeout", 0);
        dsProperties.put("maxLifetime", 0);
        dataSource.setDataSourceProperties(dsProperties);

        return dataSource;
    }
}
