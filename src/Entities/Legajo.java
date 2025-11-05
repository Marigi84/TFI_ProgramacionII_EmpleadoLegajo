
package Entities;

import java.time.LocalDate;

public class Legajo extends BaseEntity {
    private String nroLegajo;
    private String categoria;
    private Estado estado;
    private LocalDate fechaAlta;
    private String observaciones;

    public Legajo() {}

    public Legajo(Long id, Boolean eliminado, String nroLegajo, String categoria,
                  Estado estado, LocalDate fechaAlta, String observaciones) {
        super(id, eliminado);
        this.nroLegajo = nroLegajo;
        this.categoria = categoria;
        this.estado = estado;
        this.fechaAlta = fechaAlta;
        this.observaciones = observaciones;
    }

    public String getNroLegajo() { return nroLegajo; }
    public void setNroLegajo(String nroLegajo) { this.nroLegajo = nroLegajo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Legajo{" +
                "nroLegajo='" + nroLegajo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", estado=" + estado +
                ", fechaAlta=" + fechaAlta +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}

