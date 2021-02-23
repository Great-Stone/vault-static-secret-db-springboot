package com.example.vault;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import java.lang.InterruptedException;
import java.lang.Thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MyThread extends Thread {

    DataSource dataSource;

    public MyThread(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void run(){
        int count = 0;
        while(true){
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT 1");
                resultSet.next();

                System.out.println("Connection works with count: " + ++count + " result: " + resultSet.getString(1));

                resultSet.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            
        }
    }
}

@RestController
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Slf4j
public class HelloController{

    static boolean alreadyThread = false;

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void init() throws SQLException, InterruptedException {
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println(metaData.getURL());
            System.out.println(metaData.getUserName());
            System.out.println(metaData.getDriverName());
        }
        if(!alreadyThread){
            log.debug("Starting thread!");
            MyThread mt = new MyThread(dataSource);
            mt.start();
            alreadyThread = true;
        }
    }

	@RequestMapping("/")
	public String index() throws SQLException {
        
		return "Hello!";
	}

}