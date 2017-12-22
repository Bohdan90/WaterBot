package my.bot.domain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bskrypka on 22.12.2017.
 */
public class ContextConfigurationTest {
    @Autowired
    private ContextConfiguration contConf;
    @Test
    public void testHsqlDataSource() throws Exception {
        contConf= new ContextConfiguration();
        JdbcTemplate found =  new JdbcTemplate(contConf.hsqlDataSource());
        assertThat(found!=null);

    }
}