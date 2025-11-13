
package Entities;
/**
 * Clase abstracta base para todas las entidades del dominio.
 * 
 * Contiene los atributos y métodos comunes:
 * - id: identificador único
 * - eliminado: indica si el registro fue dado de baja lógica
 *
 * Aplicación del principio de reutilización mediante herencia.
 */

public abstract class BaseEntity {
    protected Long id;
    protected Boolean eliminado;

    public BaseEntity() {
        this.eliminado = false; // Por defecto los registros estan activos
    }

    public BaseEntity(Long id, Boolean eliminado) {
        this.id = id;
        this.eliminado = eliminado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getEliminado() { return eliminado; }
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }

    @Override
public String toString() {
    return getClass().getSimpleName() + " [id=" + id + ", eliminado=" + eliminado + "]";
}

}
