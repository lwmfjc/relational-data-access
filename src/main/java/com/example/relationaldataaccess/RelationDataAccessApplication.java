package com.example.relationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@SpringBootApplication
public class RelationDataAccessApplication implements CommandLineRunner {
    private static final Logger log =
            LoggerFactory.getLogger(RelationDataAccessApplication.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RelationDataAccessApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Creating tables");
        jdbcTemplate.execute("DROP TABLE customers IF EXISTS ");
        jdbcTemplate.execute("CREATE TABLE customers("
                + "id serial,first_name varchar(255),last_name varchar(255)"
                + ")");
        List<Object[]> splitUpNames = Arrays.asList("John Woo",
                "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());
        splitUpNames.forEach(name -> log.info(String.format("" +
                "Inserting customer record for %s %s", name[0], name[1])));
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name," +
                "last_name) VALUES(?,?)", splitUpNames);
        log.info("Query for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh" },
                (rs, rowNum) -> new Customer(rs.getLong("id"),
                        rs.getString("first_name"), rs.getString("last_name")))
                .forEach(customer -> log.info(customer.toString()));
    }
}
