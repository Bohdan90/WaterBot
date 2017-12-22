package my.bot.jpa.examples;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bskrypka on 22.12.2017.
 */
public class CustomerTest {
    @Autowired
    private Customer customer;
    @Test
    public void testToString() throws Exception {
        customer = new Customer("Test","Name");
assertThat(customer!=null );
    }
}