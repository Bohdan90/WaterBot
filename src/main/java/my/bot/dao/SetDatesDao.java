package my.bot.dao;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by bskrypka on 21.12.2017.
 */
public class SetDatesDao {


    public void setAllDates(String userId,String userName, String waterCount, JdbcTemplate hsqlTemplate){
        hsqlTemplate.update("INSERT INTO USERDATA VALUES ('"+userId+"','"+userName+"','"+waterCount+"')");
    }
    public void updateUserInfo(String userId,String newWaterValue, JdbcTemplate hsqlTemplate){
        hsqlTemplate.update("UPDATE USERDATA SET WATER='"+newWaterValue+"' WHERE ID='"+userId + "' ");
    }

}
