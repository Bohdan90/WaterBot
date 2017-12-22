package my.bot.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by bskrypka on 21.12.2017.
 */
public class GetResultsDao {


    public List<Map<String, Object>> getAllDates(String userId, JdbcTemplate hsqlTemplate){

        List<Map<String, Object>> list = hsqlTemplate.queryForList("Select * from USERDATA where ID='"+ userId + "'");

        return list;
    }

    public List<Map<String, Object>> getName(String userId, JdbcTemplate hsqlTemplate){

        List<Map<String, Object>> list = hsqlTemplate.queryForList("Select name from USERDATA where ID='" + userId+"'");

        return list;
    }
    public List<Map<String, Object>> getWaterCount(String userId, JdbcTemplate hsqlTemplate){

        List<Map<String, Object>> list = hsqlTemplate.queryForList("Select water from USERDATA where ID='" + userId+"'");

        return list;
    }
}
