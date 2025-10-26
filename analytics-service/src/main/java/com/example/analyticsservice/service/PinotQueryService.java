package com.example.analyticsservice.service;

import jakarta.annotation.PostConstruct;
import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.jooq.impl.DSL.*;

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

    // Helper: executes Pinot SQL query and returns first ResultSet
    private static ResultSet runQuery(Connection connection, String query) {
        ResultSetGroup resultSetGroup = connection.execute(query);
        return resultSetGroup.getResultSet(0);
    }

    // Custom inline expression builder since jOOQ doesn’t parse Pinot functions
    private static org.jooq.Field<Object> inlineExpression(String expr) {
        return field(expr);
    }

    public double getTotalRevenue(String interval) {
        String query = DSL.using(SQLDialect.POSTGRES)
                .select(sum(field("totalAmount", Double.class)).as("totalRevenue"))
                .from(tableName)
                .where(field("\"timestamp\"").ge(
                        inlineExpression("NOW() - %s".formatted(getMillis(interval)))
                ))
                .getSQL();

        ResultSet rs = runQuery(connection, query);
        return rs.getRowCount() > 0 ? rs.getDouble(0) : 0.0;
    }

    // Helper: converts human-readable interval to milliseconds expression
    private String getMillis(String interval) {
        return switch (interval.toLowerCase()) {
            case "minute" -> "60 * 1000";
            case "day" -> "24 * 3600 * 1000";
            default -> "3600 * 1000";
        };
    }

    public long getOrderCount(String interval) {
        String query = DSL.using(SQLDialect.POSTGRES)
                .select(count().as("orderCount"))
                .from(tableName)
                .where(field("\"timestamp\"").ge(
                        inlineExpression("NOW() - %s".formatted(getMillis(interval)))
                ))
                .getSQL();

        ResultSet rs = runQuery(connection, query);
        return rs.getRowCount() > 0 ? rs.getLong(0) : 0L;
    }
}
