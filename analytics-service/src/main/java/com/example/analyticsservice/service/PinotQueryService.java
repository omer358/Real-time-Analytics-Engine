package com.example.analyticsservice.service;

import jakarta.annotation.PostConstruct;
import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PinotQueryService {

    @Value("${pinot.hosts}")
    private String pinotHosts;

    @Value("${pinot.table}")
    private String tableName;

    private Connection connection;

    @PostConstruct
    public void init() {
        this.connection = ConnectionFactory.fromHostList(pinotHosts);
    }

    public double getTotalRevenue(String interval) {
        String sql = "SELECT SUM(totalAmount) AS totalRevenue FROM " + tableName +
                " WHERE \"timestamp\" >= " + timestampAgo(interval);
        return executeNumericQuery(sql, 0.0);
    }

    public long getOrderCount(String interval) {
        String sql = "SELECT COUNT(*) AS orderCount FROM " + tableName +
                " WHERE \"timestamp\" >= " + timestampAgo(interval);
        return (long) executeNumericQuery(sql, 0L);
    }

    // Helper: executes Pinot SQL and returns single numeric value
    private double executeNumericQuery(String sql, double defaultValue) {
        ResultSetGroup group = connection.execute(sql);
        ResultSet rs = group.getResultSet(0);
        if (rs.getRowCount() > 0) {
            return rs.getDouble(0); // <-- use getDouble for numeric
        }
        return defaultValue;
    }

    // Helper: computes timestamp in millis
    private long timestampAgo(String interval) {
        long millis = switch (interval.toLowerCase()) {
            case "minute" -> 60_000L;
            case "day" -> 24 * 3600_000L;
            default -> 3600_000L; // default 1 hour
        };
        return System.currentTimeMillis() - millis;
    }
}
