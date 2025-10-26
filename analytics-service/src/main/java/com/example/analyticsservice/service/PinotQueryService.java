package com.example.analyticsservice.service;

import groovy.util.logging.Slf4j;
import jakarta.annotation.PostConstruct;
import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.jooq.impl.DSL.field;

@Service
@Slf4j
public class PinotQueryService {

    private static final Logger log = LoggerFactory.getLogger(PinotQueryService.class);
    @Value("${pinot.hosts}")
    private String pinotHosts;

    @Value("${pinot.table}")
    private String tableName;

    private Connection connection;

    // Helper: executes Pinot SQL query and returns first ResultSet
    private static ResultSet runQuery(Connection connection, String query) {
        ResultSetGroup resultSetGroup = connection.execute(query);
        return resultSetGroup.getResultSet(0);
    }

    // Custom inline expression builder since jOOQ doesn’t parse Pinot functions
    private static org.jooq.Field<Object> inlineExpression(String expr) {
        return field(expr);
    }

    @PostConstruct
    public void init() {
        this.connection = ConnectionFactory.fromHostList(pinotHosts);
    }

    public double getTotalRevenue(String interval) {
        long millisAgo = System.currentTimeMillis() - getMillisAsLong(interval);
        String query = "SELECT SUM(totalAmount) AS totalRevenue FROM " + tableName +
                " WHERE \"timestamp\" >= " + millisAgo;

        ResultSet rs = runQuery(connection, query);
        return rs.getRowCount() > 0 ? rs.getDouble(0) : 0.0;
    }

    public long getOrderCount(String interval) {
        long millisAgo = System.currentTimeMillis() - getMillisAsLong(interval);
        String query = "SELECT COUNT(*) AS orderCount FROM " + tableName +
                " WHERE \"timestamp\" >= " + millisAgo;

        ResultSet rs = runQuery(connection, query);
        return rs.getRowCount() > 0 ? rs.getLong(0) : 0L;
    }

    private long getMillisAsLong(String interval) {
        return switch (interval.toLowerCase()) {
            case "minute" -> 60_000L;
            case "day" -> 24 * 3600_000L;
            default -> 3600_000L;
        };
    }

    // Helper: converts human-readable interval to milliseconds expression
    private String getMillis(String interval) {
        return switch (interval.toLowerCase()) {
            case "minute" -> "60 * 1000";
            case "day" -> "24 * 3600 * 1000";
            default -> "3600 * 1000";
        };
    }
}
