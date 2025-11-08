
package Entities;

import java.time.LocalDate;

public class Empleado extends BaseEntity {
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private LocalDate fechaIngreso;
    private String area;
    private Legajo legajo; // Relacion 1:1 unidireccional

    public Empleado() {}

    public Empleado(Long id, Boolean eliminado, String nombre, String apellido, String dni,
                    String email, LocalDate fechaIngreso, String area, Legajo legajo) {
        super(id, eliminado);
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaIngreso = fechaIngreso;
        this.area = area;
        this.legajo = legajo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public Legajo getLegajo() { return legajo; }
    public void setLegajo(Legajo legajo) { this.legajo = legajo; }

    @Override
    public String toString() {
        return "Empleado{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", area='" + area + '\'' +
                ", legajo=" + (legajo != null ? legajo.getNroLegajo() : "sin legajo") +
                '}';
    }
}
