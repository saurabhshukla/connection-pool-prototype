package org.prototype.connectionpool;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.prototype.Constants.GREEN;
import static org.prototype.Constants.RESET;

/**
 * A class to demonstrate the usage of a database connection pool for sequential and parallel query execution.
 * <p>
 * This class manages the database connection pool and query execution using both sequential and parallel methods.
 * It also handles the cleanup of resources after execution.
 * </p>
 */
public class ConnectionPool {
    private final DBConnectionPool _dbConnectionPool;
    private final QueryDBData _queryDBData;

    /**
     * Constructs a ConnectionPool instance.
     * Initializes the database connection pool and query handler.
     */
    public ConnectionPool() {
        this._dbConnectionPool = new DBConnectionPool();
        this._queryDBData = new QueryDBData(_dbConnectionPool);
    }

    /**
     * Executes database queries in parallel and measures the execution time.
     * <p>
     * This method creates a fixed thread pool with 5 threads and runs the {@link QueryDBData#fetchDataFromDB(int, boolean)}
     * method in parallel for 5 iterations. It prints the time taken for the parallel execution.
     * </p>
     *
     * @param connectionCount - Total number of connections to make
     * @param useConnectionPool - Whether to use connection pool
     */
    public void runInParallel(int connectionCount, boolean useConnectionPool) {
        // Create a fixed thread pool with 150 threads
        ExecutorService executorService = Executors.newFixedThreadPool(150);

        long startTime = System.nanoTime();
        for (int i = 0; i < connectionCount; i++) {
            final int index = i;
            executorService.submit(() -> _queryDBData.fetchDataFromDB(index, useConnectionPool));
        }

        // Shutdown the executor service
        executorService.shutdown();
        try {
            // Wait for all tasks to complete or timeout after 1 minute
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println(GREEN + "Execution time: " + duration / 1_000_000 + " ms" + RESET);
    }

    /**
     * Shuts down the database connection pool and releases resources.
     * <p>
     * This method ensures that all connections in the pool are properly closed.
     * </p>
     */
    public void tearDown() {
        try {
            _dbConnectionPool.shutdown();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
