package org.prototype.connectionpool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A class for querying data from a database using a connection pool.
 * <p>
 * The class utilizes a {@link DBConnectionPool} to manage database connections and execute queries.
 * It demonstrates fetching data from a database table and handling the results in a thread-safe manner.
 * </p>
 */
public class QueryDBData {
    private final DBConnectionPool _connectionPool;

    /**
     * Constructs a QueryDBData object with the specified connection pool.
     *
     * @param connectionPool the {@link DBConnectionPool} used to obtain database connections
     */
    public QueryDBData(DBConnectionPool connectionPool) {
        this._connectionPool = connectionPool;
    }

    /**
     * Fetches data from the database and prints it to the console.
     * <p>
     * This method retrieves all records from the "user" table and prints the values of "id" and "name" columns.
     * It ensures thread-safe printing by synchronizing on {@code System.out}.
     * </p>
     *
     * @param index an identifier for the current connection or thread
     */
    public void fetchDataFromDB(int index, boolean useConnectionPool) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // Establish connection
            connection = useConnectionPool ? _connectionPool.getConnection() : _connectionPool.createNewConnection();

            // Create a statement
            statement = connection.createStatement();

            // Execute a query
            String sql = "SELECT * FROM user";
            resultSet = statement.executeQuery(sql);

            // Process the result set
            while (resultSet.next()) {
                // Method 1: Use synchronized block to ensure thread-safe printing
                synchronized (System.out) {
                    System.out.println("-----------------------------------------");
                    System.out.println("Fetching data via connection object " + index);
                    System.out.println("-----------------------------------------");
                    System.out.println("Column 1: " + resultSet.getString("id"));
                    System.out.println("Column 2: " + resultSet.getString("name"));
                    System.out.println("-----------------------------------------\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) {
                    if (useConnectionPool) {
                        _connectionPool.releaseConnection(connection);
                    } else {
                        connection.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
