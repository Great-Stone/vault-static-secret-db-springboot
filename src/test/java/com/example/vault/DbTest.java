package com.example.vault;

import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class DbTest {

	@Autowired
	DataSource dataSource;
	
	@Test
	public void connection() throws SQLException {
		try(Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			log.info("URL : " + metaData.getURL());
			log.info("DriverName : " + metaData.getDriverName());
			log.info("UserNmae : " + metaData.getUserName());
		}
	}
}