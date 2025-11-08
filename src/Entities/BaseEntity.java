
package Entities;

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
        return "ID=" + id + ", eliminado=" + eliminado;
    }
}
