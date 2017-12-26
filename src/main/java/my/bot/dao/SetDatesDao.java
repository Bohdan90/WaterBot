package my.bot.dao;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by bskrypka on 21.12.2017.
 */
public class SetDatesDao {


    public void setAllDates(String userId,String userName, String waterCount,String notificationsFreq, JdbcTemplate hsqlTemplate){
        hsqlTemplate.update("INSERT INTO USERDATA VALUES ('"+userId+"','"+userName+"','"+waterCount+"','"+notificationsFreq+"')");
    }
    public void updateUserWatCons(String userId,String newWaterValue, JdbcTemplate hsqlTemplate){
        hsqlTemplate.update("UPDATE USERDATA SET WATER='"+newWaterValue+"' WHERE ID='"+userId + "' ");
    }
    public void updateUserNotFreq(String userId,String newFreq, JdbcTemplate hsqlTemplate){
        hsqlTemplate.update("UPDATE USERDATA SET NOTIFICATIONS='"+newFreq+"' WHERE ID='"+userId + "' ");
    }
}
