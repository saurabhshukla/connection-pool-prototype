package org.prototype.connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.prototype.Constants.RED;
import static org.prototype.Constants.RESET;

/**
 * A connection pool for managing and reusing database connections.
 * <p>
 * This class manages a pool of database connections to optimize performance and resource usage.
 * It supports acquiring and releasing connections, and ensures that the number of active connections
 * does not exceed the maximum pool size.
 * </p>
 */
public class DBConnectionPool {
    private final String _jdbcUrl;
    private final String _username;
    private final String _password;
    private final int _initialPoolSize;
    private final int _maxPoolSize;
    private final BlockingQueue<Connection> _availableConnections;
    private final BlockingQueue<Connection> _usedConnections;

    /**
     * Constructs a DBConnectionPool with configuration properties loaded from a file.
     * <p>
     * The constructor initializes the connection pool with the specified size and database credentials.
     * </p>
     */
    public DBConnectionPool() {
        ConfigLoader configLoader = new ConfigLoader("config.properties");
        this._jdbcUrl = configLoader.getProperty("jdbc.url");
        this._username = configLoader.getProperty("jdbc.username");
        this._password = configLoader.getProperty("jdbc.password");
        this._initialPoolSize = configLoader.getIntProperty("pool.initialSize");
        this._maxPoolSize = configLoader.getIntProperty("pool.maxSize");
        this._availableConnections = new ArrayBlockingQueue<>(_maxPoolSize);
        this._usedConnections = new ArrayBlockingQueue<>(_maxPoolSize);

        try {
            for (int i = 0; i < _initialPoolSize; i++) {
                _availableConnections.add(createNewConnection());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new database connection using the configured JDBC URL, username, and password.
     *
     * @return a new {@link Connection} object
     * @throws SQLException if a database access error occurs
     */
    public Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(_jdbcUrl, _username, _password);
    }

    /**
     * Retrieves a connection from the pool.
     * <p>
     * This method waits up to 15 seconds for a connection to become available. If no connection is available
     * and the maximum pool size has not been reached, a new connection is created.
     * If the maximum pool size is reached, an exception is thrown.
     * </p>
     *
     * @return a {@link Connection} object from the pool
     * @throws SQLException if the maximum pool size is reached or if interrupted while waiting
     */
    public Connection getConnection() throws SQLException {
        try {
            // Wait up to 15 seconds for a connection to become available
            Connection connection = _availableConnections.poll(15, TimeUnit.SECONDS);

            if (connection == null) {
                if (_usedConnections.size() < _maxPoolSize) {
                    connection = createNewConnection();
                } else {
                    throw new SQLException(RED + "Maximum pool size reached, no available connections!" + RESET);
                }
            }

            _usedConnections.add(connection);
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new SQLException(RED + "Interrupted while waiting for a connection" + RESET, e);
        }
    }

    /**
     * Releases a connection back to the pool.
     * <p>
     * This method returns the connection to the pool of available connections, making it ready for reuse.
     * </p>
     *
     * @param connection the {@link Connection} object to be released
     */
    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            _usedConnections.remove(connection);
            _availableConnections.offer(connection);
        }
    }

    /**
     * Returns the number of available connections in the pool.
     *
     * @return the number of available connections
     */
    public int getAvailableConnectionsCount() {
        return _availableConnections.size();
    }

    /**
     * Closes all connections in the pool and releases associated resources.
     * <p>
     * This method should be called to shut down the pool and close all connections before application exit.
     * </p>
     *
     * @throws SQLException if a database access error occurs while closing the connections
     */
    public synchronized void shutdown() throws SQLException {
        for (Connection connection : _availableConnections) {
            connection.close();
        }
        for (Connection connection : _usedConnections) {
            connection.close();
        }
    }
}
