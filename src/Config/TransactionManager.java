package Config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestor de transacciones que centraliza el manejo de commit/rollback.
 * Implementa AutoCloseable para garantizar rollback automático con try-with-resources.
 * 
 * Simplifica el código de los Services eliminando la repetición del patrón
 * setAutoCommit(false) → commit() → rollback() → close().
 */
public class TransactionManager implements AutoCloseable {
    private Connection conn;
    private boolean transactionActive;

    /**
     * Constructor que inicializa el gestor con una conexión.
     *
     * @param conn Conexión a la base de datos
     * @throws IllegalArgumentException Si la conexión es null
     */
    public TransactionManager(Connection conn) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        this.conn = conn;
        this.transactionActive = false;
    }

    /**
     * Obtiene la conexión administrada por este TransactionManager.
     *
     * @return Conexión a la base de datos
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Inicia una transacción desactivando el autocommit.
     *
     * @throws SQLException Si la conexión está cerrada
     */
    public void startTransaction() throws SQLException {
        if (conn == null) {
            throw new SQLException("No se puede iniciar la transacción: conexión no disponible");
        }
        if (conn.isClosed()) {
            throw new SQLException("No se puede iniciar la transacción: conexión cerrada");
        }
        conn.setAutoCommit(false);
        transactionActive = true;
    }

    /**
     * Confirma la transacción (commit).
     *
     * @throws SQLException Si no hay transacción activa
     */
    public void commit() throws SQLException {
        if (conn == null) {
            throw new SQLException("Error al hacer commit: no hay conexión establecida");
        }
        if (!transactionActive) {
            throw new SQLException("No hay una transacción activa para hacer commit");
        }
        conn.commit();
        transactionActive = false;
    }

    /**
     * Revierte la transacción (rollback).
     * Generalmente invocado automáticamente por close() si no se hizo commit.
     */
    public void rollback() {
        if (conn != null && transactionActive) {
            try {
                conn.rollback();
                transactionActive = false;
            } catch (SQLException e) {
                System.err.println("Error durante el rollback: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra la transacción y la conexión de forma segura.
     * Invocado automáticamente por try-with-resources.
     * 
     * Si la transacción sigue activa, hace rollback automático.
     */
    @Override
    public void close() {
        if (conn != null) {
            try {
                if (transactionActive) {
                    rollback();
                }
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si hay una transacción activa.
     *
     * @return true si hay transacción activa, false en caso contrario
     */
    public boolean isTransactionActive() {
        return transactionActive;
    }
}
