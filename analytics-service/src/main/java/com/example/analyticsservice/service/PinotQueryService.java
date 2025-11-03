package com.example.analyticsservice.service;

import jakarta.annotation.PostConstruct;
import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    /**
     * Returns a time-series of order counts for a given interval
     * @param interval "minute", "hour", or "day"
     */
    public List<Map<String,Object>> getOrderCountTimeSeries(String interval) {
        long bucketSize = timestampAgo(interval);

        long startTime = System.currentTimeMillis() - bucketSize * 60; // last 60 buckets as example

        String sql = String.format(
                "SELECT FLOOR(\"timestamp\"/%d)*%d AS bucket, COUNT(*) AS orderCount " +
                        "FROM %s " +
                        "WHERE \"timestamp\" >= %d " +
                        "GROUP BY bucket " +
                        "ORDER BY bucket ASC",
                bucketSize, bucketSize, tableName, startTime
        );

        ResultSetGroup group = connection.execute(sql);
        ResultSet rs = group.getResultSet(0);
        List<Map<String,Object>> trend = new ArrayList<>();

        for (int i = 0; i < rs.getRowCount(); i++) {
            double bucketDouble = rs.getDouble(i, 0); // bucket column
            long bucketMillis = (long) bucketDouble;  // convert to long
            long orderCount = rs.getLong(i, 1);

            trend.add(Map.of(
                    "timestamp", Instant.ofEpochMilli(bucketMillis).toString(),
                    "orderCount", orderCount
            ));
        }
        return trend;
    }

    public double getAverageOrderValue(String interval) {
        String sql = String.format(
                "SELECT SUM(totalAmount) AS total, COUNT(*) AS orders FROM %s WHERE \"timestamp\" >= %d",
                tableName, timestampAgo(interval)
        );

        ResultSetGroup group = connection.execute(sql);
        ResultSet rs = group.getResultSet(0);

        if (rs.getRowCount() == 0) return 0.0;

        double total = rs.getDouble(0, 0);
        double orders = rs.getDouble(0, 1);
        if (orders == 0) return 0.0;

        return total / orders;
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
