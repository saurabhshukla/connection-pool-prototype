package org.prototype;

import org.prototype.connectionpool.ConnectionPool;
import org.prototype.connectionpool.DBConnectionPool;

/**
 * The main class to test the functionality of the various prototypes.
 * <p>
 * This class serves as the entry point for the application .
 * </p>
 */
public class Main {
    public static void main(String[] args) {
        // Test DB connections with connection pool for 500 connections
        testConnectionPool(500, true);

        // Test DB connections without connection pool for 500 connections
        testConnectionPool(500, false);
    }

    /**
     * Tests the functionality of the {@link DBConnectionPool}.
     * <p>
     * This method initializes the connection pool, executes database queries sequentially and in parallel to measure
     * performance, and then cleans up resources by shutting down the connection pool.
     * </p>
     *
     * @param connectionCount - Total number of connections to make
     * @param useConnectionPool - Whether to use connection pool
     */
    private static void testConnectionPool(int connectionCount, boolean useConnectionPool) {
        // Initialize the ConnectionPool instance
        ConnectionPool connectionPool = new ConnectionPool();

        // Run database queries in parallel and measure execution time
        connectionPool.runInParallel(connectionCount, useConnectionPool);

        // Teardown DB connections and release resources
        connectionPool.tearDown();
    }
}