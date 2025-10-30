package com.example.analyticsservice.service;

import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PinotQueryServiceTest {

    private PinotQueryService service;

    @Mock
    private Connection connection;

    @Mock
    private ResultSetGroup resultSetGroup;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PinotQueryService();
        ReflectionTestUtils.setField(service, "tableName", "orders");
        ReflectionTestUtils.setField(service, "pinotHosts", "localhost:9000");
        ReflectionTestUtils.setField(service, "connection", connection);
    }

    @Test
    void testGetTotalRevenue_withData_returnsResult() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(1);
        when(resultSet.getDouble(0)).thenReturn(500.0);

        double result = service.getTotalRevenue("hour");
        assertEquals(500.0, result);
    }

    @Test
    void testGetTotalRevenue_emptyResult() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(0);

        double result = service.getTotalRevenue("hour");
        assertEquals(0.0, result);
    }

    @Test
    void testGetOrderCount_withData() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(1);
        when(resultSet.getDouble(0)).thenReturn(12.0);

        long count = service.getOrderCount("minute");
        assertEquals(12L, count);
    }

    @Test
    void testGetOrderCountTimeSeries_withData() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(2);
        when(resultSet.getDouble(0, 0)).thenReturn((double) System.currentTimeMillis());
        when(resultSet.getLong(0, 1)).thenReturn(10L);
        when(resultSet.getDouble(1, 0)).thenReturn((double) System.currentTimeMillis() + 60000);
        when(resultSet.getLong(1, 1)).thenReturn(15L);

        List<Map<String, Object>> result = service.getOrderCountTimeSeries("minute");
        assertEquals(2, result.size());
        assertTrue(result.get(0).containsKey("timestamp"));
        assertEquals(10L, result.get(0).get("orderCount"));
    }

    @Test
    void testGetAverageOrderValue_withData() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(1);
        when(resultSet.getDouble(0, 0)).thenReturn(1000.0);
        when(resultSet.getDouble(0, 1)).thenReturn(10.0);

        double result = service.getAverageOrderValue("day");
        assertEquals(100.0, result);
    }

    @Test
    void testGetAverageOrderValue_zeroOrders() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(1);
        when(resultSet.getDouble(0, 0)).thenReturn(500.0);
        when(resultSet.getDouble(0, 1)).thenReturn(0.0);

        double result = service.getAverageOrderValue("day");
        assertEquals(0.0, result);
    }

    @Test
    void testGetAverageOrderValue_emptyResult() {
        when(connection.execute(anyString())).thenReturn(resultSetGroup);
        when(resultSetGroup.getResultSet(0)).thenReturn(resultSet);
        when(resultSet.getRowCount()).thenReturn(0);

        double result = service.getAverageOrderValue("hour");
        assertEquals(0.0, result);
    }

    @Test
    void testTimestampAgo_minute_hour_day() {
        long now = System.currentTimeMillis();
        long minuteAgo = (long) ReflectionTestUtils.invokeMethod(service, "timestampAgo", "minute");
        long hourAgo = (long) ReflectionTestUtils.invokeMethod(service, "timestampAgo", "hour");
        long dayAgo = (long) ReflectionTestUtils.invokeMethod(service, "timestampAgo", "day");

        assertTrue(minuteAgo <= now);
        assertTrue(hourAgo <= now);
        assertTrue(dayAgo <= now);
    }

    @Test
    void testInit_createsConnection() {
        PinotQueryService newService = new PinotQueryService();
        ReflectionTestUtils.setField(newService, "pinotHosts", "localhost:9000");
        ConnectionFactory mockFactory = mock(ConnectionFactory.class);
        // mocking static not needed; this just checks that init() runs
        assertDoesNotThrow(newService::init);
    }
}
