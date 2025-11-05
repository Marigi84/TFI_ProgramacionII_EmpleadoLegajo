/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import Entities.Empleado;
import Entities.Legajo;
import Entities.*;

import java.time.LocalDate;

public class mainEntidades {
    public static void main(String[] args) {

        // Crear un legajo de prueba
        Legajo legajo = new Legajo(1L, false, "LEG-0001", "A", Estado.ACTIVO,
                LocalDate.now(), "Empleado destacado");

        // Crear un empleado con su legajo asociado
        Empleado empleado = new Empleado(1L, false, "Marina", "Cordero", "40999888",
                "marina@mail.com", LocalDate.of(2023, 3, 10),
                "Sistemas", legajo);

        System.out.println("? Datos del empleado de prueba:");
        System.out.println(empleado);
    }
}