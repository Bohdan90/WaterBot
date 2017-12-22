package my.bot.domain;

/**
 * Created by bskrypka on 20.12.2017.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Spring programmatic context configuration.
 */
@Configuration
public class ContextConfiguration {
    /**
     * Create HSQLDB data source.
     * @return hsql database bean.
     */


    @Bean
    public DataSource hsqlDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
                .setType(EmbeddedDatabaseType.H2) //.H2 or .DERBY
                .addScript("schema.sql")
              //  .addScript("h2-data.sql")
                .build();
        return db;
    }

    /**
     * Create jdbc template for HSQLDB.
     * @return hsql database jdbc template.
     */
    @Bean
    public JdbcTemplate hsqlTemplate() {
        return new JdbcTemplate(hsqlDataSource());
    }
}
