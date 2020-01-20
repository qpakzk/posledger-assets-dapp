package com.poscoict.assets.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/resources/root-context.xml"})
public class MYSQLConnectionTest {

    @Autowired
    private DataSource ds;

    @Test
    public void testConnection() throws Exception {

        try (Connection con = ds.getConnection()) {
            System.out.println("\n >>>>>>>> Connection : " + con + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
