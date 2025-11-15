
package Main;



import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import Entities.Empleado;
import Entities.Legajo;
import Service.EmpleadoServiceImpl;
import Service.LegajoServiceImpl;
import Entities.Estado;



public class AppMenu {
    public static void main(String[] args) {
        Scanner scan = new Scanner (System.in);
        
        /*
        Instancias de la capa Servicios encargadas de la lógica de negocio
        */
        
        EmpleadoServiceImpl empleadoService = new EmpleadoServiceImpl();
        LegajoServiceImpl legajoService = new LegajoServiceImpl();
         
        int opcion = -1;
        do{
            
            // ***********Inicio de Menu principal****************
            
            try{
                menu();
                opcion = Integer.parseInt(scan.nextLine());
                switch(opcion){
                    
            //**************Se convoca a las funciones insertar y crearEmpleadoConLegajo de la capa Servicios*************
            //**************Esta implementación permite crear un empleado de manera independiente o crearlo junto con su legajo*************
                    
                    case 1 -> {
                        try{
                            Empleado empleado = crearEmpleado();
                            String nuevoLegajo;
                            do {
                                System.out.println("¿Desea crear un legajo para este empleado (s/n)?: ");
                                nuevoLegajo = scan.nextLine();                                
                                if (!nuevoLegajo.equalsIgnoreCase("s") && !nuevoLegajo.equalsIgnoreCase("n")) {
                                    System.out.println("Opción no válida. Ingrese 's' para sí o 'n' para no.");
                                }                                
                            } while (!nuevoLegajo.equalsIgnoreCase("s") && !nuevoLegajo.equalsIgnoreCase("n"));    
                                if(nuevoLegajo.equalsIgnoreCase("n")){ // Inserta el empleado sin legajo
                                empleadoService.insertar(empleado);
                            } else { 
                                Legajo legajo = crearLegajo();
                                empleado.setLegajo(legajo);
                                empleadoService.crearEmpleadoConLegajo(empleado); // Crea un legajo y lo asocia al empleado antes de insertarlo
                            }
                        }catch (Exception e){
                            System.err.println("Error al insertar los datos del empleado: " + e.getMessage());
                        }
                    }

            //**************Se aplican las funciones de lectura definidas para las dos entidades en la capa Servicios***************
            //**************Permite al usuario buscar empleados o legajos por distintos criterios o listar todos los registros*********
                    
                    case 2 -> {                        
                        try{
                            int opcBusqueda;                           
                            do{
                                System.out.println("Seleccione la opción deseada: ");
                                System.out.println("1. BUSCAR EMPLEADO POR DNI.");
                                System.out.println("2. BUSCAR EMPLEADO POR ID.");
                                System.out.println("3. BUSCAR LEGAJO POR ID.");
                                System.out.println("4. LISTAR TODOS LOS EMPLEADOS ACTIVOS.");
                                System.out.println("5. LISTAR TODOS LOS LEGAJOS.");
                                System.out.println("6. VOLVER.");
                                if(scan.hasNextInt()){
                                    opcBusqueda = scan.nextInt();
                                    scan.nextLine();
                                        switch (opcBusqueda){
                                            case 1 -> {  // Busca un empleado por su DNI
                                                System.out.println("Ingrese el DNI del empleado: ");
                                                String dni = scan.nextLine();
                                                Empleado empleadoBusquedaDni = empleadoService.buscarPorDni(dni);
                                                if(empleadoBusquedaDni == null){
                                                    System.out.println("No se encontraron registros.");
                                                }else{
                                                    System.out.println(empleadoBusquedaDni);
                                                }
                                            }
                                            case 2 -> { // Busca un empleado por su ID
                                                System.out.println("Ingrese el ID del empleado: ");
                                                Long id = scan.nextLong();
                                                Empleado empleadoBusquedaId = empleadoService.getById(id);
                                                if(empleadoBusquedaId == null){
                                                    System.out.println("No se encontraron registros.");
                                                }else{
                                                    System.out.println(empleadoBusquedaId);
                                                }
                                            }

                                            
                                            case 3 ->{ // Busca un legajo por su ID
                                                System.out.println("Ingrese el ID del legajo");
                                                Long id = scan.nextLong();
                                                Legajo legajoBusqueda = legajoService.getById(id);
                                                if(legajoBusqueda == null){
                                                    System.out.println("No se encontraron registros.");
                                                }else{
                                                    System.out.println(legajoBusqueda);
                                                }
                                            }
                                            
                                            case 4 -> {  // Lista todos los empleados activos
                                                System.out.println(empleadoService.getAll());
                                            }
                                            
                                            case 5 -> { // Lista todos los legajos
                                                System.out.println(legajoService.getAll());
                                            }
                                            
                                            case 6 -> {  // Vuelve al menú principal
                                                break;
                                            }
                                        }
                                } else{
                                    // Manejo de errores si el usuario ingresa algo que no sea número
                                    System.out.println("Error: Debe ingresar un número entero."); 
                                    scan.nextLine(); 
                                    opcBusqueda = -1; 
                                }                                    
                            }while(opcBusqueda < 4 && opcBusqueda > 0);
                        }
                        catch(Exception e){
                            System.err.println("Se ha producido un error en la búsqueda." + e.getMessage());
                        }
                    }
                    
                    
            //******************Permite modificar datos de empleados o legajos existentes****************
                    case 3 -> {
                        try{                            
                            int opcActualizar;
                            String opcLegajoNuevo;
                            do{
                                System.out.println("Seleccione el tipo de información que desea actualizar: ");
                                System.out.println("1. EMPLEADOS");
                                System.out.println("2. LEGAJOS");
                                System.out.println("3. VOLVER");
                                if(scan.hasNextInt()){
                                    opcActualizar = scan.nextInt();
                                    scan.nextLine();
                                    switch (opcActualizar){
                                        case 1 -> { // Actualizar datos de un empleado existente
                                            System.out.println("Ingrese el ID del empleado");
                                            Long id = scan.nextLong();
                                            Empleado empleadoActualizar = empleadoService.getById(id);
                                            scan.nextLine();
                                            if(empleadoActualizar == null){
                                                System.out.println("No se encontraron registros con ese ID.");
                                                opcActualizar = -1;
                                            }else{
                                                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                                 
                                                //Se solicita al usuario los nuevos datos del empleado
                                                
                                                LocalDate nuevaFecha = null;       
                                                System.out.println("Ingrese el nuevo nombre " + "("+empleadoActualizar.getNombre()+"):");
                                                String nuevoNombre = scan.nextLine();
                                                System.out.println("Ingrese el nuevo apellido " + "("+empleadoActualizar.getApellido()+"):");
                                                String nuevoApellido = scan.nextLine();
                                                System.out.println("Ingrese el nuevo email " + "("+empleadoActualizar.getEmail()+")");
                                                String nuevoEmail = scan.nextLine();
                                                System.out.println("Indique una nueva fecha de ingreso (yyyy-MM-dd)" + "-"+empleadoActualizar.getFechaIngreso()+"-");
                                                String fechaInput = scan.nextLine();
                                                nuevaFecha = LocalDate.parse(fechaInput, formato);
                                                System.out.println("Ingrese una nueva area " + "("+empleadoActualizar.getArea()+")");
                                                String nuevaArea = scan.nextLine();
                                                
                                                // Se crea un objeto Empleado actualizado y lo envía al servicio
                                                
                                                Empleado empleadoActualizado = new Empleado(empleadoActualizar.getId(),empleadoActualizar.getEliminado(),nuevoNombre,nuevoApellido,empleadoActualizar.getDni(),nuevoEmail,nuevaFecha,nuevaArea,empleadoActualizar.getLegajo());
                                                empleadoService.actualizar(empleadoActualizado);
                                            }   
                                        }
                                        case 2 -> { // Actualiza datos de un legajo existente
                                            System.out.println("Ingrese el ID del legajo: ");
                                            Long idLegajo = scan.nextLong();
                                            Legajo legajoActualizar = legajoService.getById(idLegajo);
                                            scan.nextLine();
                                            if(legajoActualizar == null){
                                                System.out.println("No se encontraron registros con ese ID.");
                                                opcActualizar = -1;
                                            }else{
                                                Estado estadoLegajo = null;
                                                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                                LocalDate nuevaFechaAlta = null;
                                                System.out.println("Ingrese el nuevo número de legajo " + "("+legajoActualizar.getNroLegajo()+"):");
                                                String nuevoNumero = scan.nextLine();
                                                System.out.println("Ingrese una categoría " + "("+legajoActualizar.getCategoria()+"):");
                                                String nuevaCategoria = scan.nextLine();
                                                System.out.println("Indique una nueva fecha de alta (yyyy-MM-dd)" + "-"+legajoActualizar.getFechaAlta()+"-");
                                                String fechaInput = scan.nextLine();
                                                nuevaFechaAlta = LocalDate.parse(fechaInput, formato);
                                                System.out.println("Ingrese nuevas observaciones: ");
                                                String nuevaObservacion = scan.nextLine();
                                                System.out.println("");
                                                int opcEstado = -1;
                                                do{
                                                    System.out.println("Actualice el estado " + "("+legajoActualizar.getEstado()+")");
                                                    System.out.println("1. ACTIVO");
                                                    System.out.println("2. INACTIVO");
                                                    System.out.println("3. CANCELAR");                                               
                                                    if (scan.hasNextInt()) {
                                                        opcEstado = scan.nextInt();                                                        
                                                        switch (opcEstado) {
                                                            case 1: estadoLegajo = Estado.ACTIVO; break;
                                                            case 2: estadoLegajo = Estado.INACTIVO; break;
                                                            case 3: 
                                                                System.out.println("Operación cancelada.");
                                                                menu();          
                                                        }
                                                    }else{
                                                        System.out.println("Ingrese un opción válida.");
                                                        scan.nextLine(); 
                                                        opcEstado = -1;
                                                    }
                                                }while(opcEstado < 0 && opcEstado > 3); 
                                                
                                                // Se crea un objeto Legajo actualizado y se envía al servicio
                                                
                                                Legajo legajoActualizado = new Legajo(legajoActualizar.getId(),legajoActualizar.getEliminado(),nuevoNumero,nuevaCategoria,estadoLegajo,nuevaFechaAlta,nuevaObservacion);
                                                legajoService.actualizar(legajoActualizado);
                                            }   
                                        }
                                        case 3 -> {
                                            break;
                                        }
                                    }                            
                                }else{
                                    System.out.println("Ingrese una opción valida.");
                                    scan.nextLine(); 
                                    opcActualizar = -1;
                                }
                            }while(opcActualizar > 0 && opcActualizar < 3);
                        }
                        catch (Exception e){
                            System.err.println("Se produjo un error al acualizar los datos: " + e.getMessage());
                        }                        
                    } 
                    
                    
             //*************Permite borrar empleados o legajos existentes****************
                    
                    case 4 ->{
                        try{
                            int opcEliminar = 0;
                            Long idEliminar;
                            do{
                                System.out.println("Seleccione el tipo de información que quiere eliminar: ");
                                System.out.println("1. EMPLEADOS");
                                System.out.println("2. LEGAJOS");
                                System.out.println("3. VOLVER");
                                if(scan.hasNextInt()){
                                    opcEliminar = scan.nextInt();
                                    if(opcEliminar == 1 || opcEliminar == 2 || opcEliminar == 3){
                                        switch(opcEliminar){
                                            case 1 -> {
                                                System.out.println("Ingrese el ID del empleado que desea eliminar");
                                                idEliminar = scan.nextLong();
                                                empleadoService.eliminar(idEliminar);
                                            }
                                            case 2 -> {
                                                System.out.println("Ingrese el ID del legajo que desea eliminar");
                                                idEliminar = scan.nextLong();
                                                legajoService.eliminar(idEliminar);
                                            }
                                            case 3 -> {                                                
                                                break;
                                            }
                                        }
                                        scan.nextLine();
                                    }else{
                                        System.out.println("Seleccione una opcion válida: ");
                                    }                                    
                                }
                            }while(opcEliminar<1 || opcEliminar>3);
                        }                        
                        catch(Exception e){
                            System.err.println("Se ha producido un error al eliminar la información: " + e.getMessage());
                        }
                    }
                        
                }
            }catch (Exception v){
                System.err.println("Error: " + v.getMessage());
            }    
        }while (opcion !=0);    
    }
     
    // Muestra el menú principal con todas las opciones disponibles al usuario
    public static void menu(){
        System.out.println("==========MENU==========");
        System.out.println("1. CREAR EMPLEADO");
        System.out.println("2. BUSCAR REGISTROS");
        System.out.println("3. ACTUALIZAR REGISTROS");
        System.out.println("4. ELIMINAR REGISTROS");
        System.out.println("0. SALIR");
        System.out.println("Ingrese un opción: ");                
    }
    
    // Solicita al usuario los datos de un empleado y devuelve un objeto Empleado
    public static Empleado crearEmpleado(){
        Scanner scan = new Scanner (System.in);
        System.out.println("Nombre: ");
        String nombre = scan.nextLine();
        System.out.println("Apellido: ");
        String apellido = scan.nextLine();
        System.out.println("DNI: ");
        String dni = scan.nextLine();
        System.out.println("Email: ");
        String email = scan.nextLine();
        System.out.println("Area: ");
        String area = scan.nextLine();
        
        //return new Empleado(0L,false,nombre,apellido,dni,email,LocalDate.now(),area,crearLegajo());
        return new Empleado(0L,false,nombre,apellido,dni,email,LocalDate.now(),area,null);
    }
    
        
    
    
    // Solicita al usuario los datos de un legajo y devuelve un objeto Legajo
    public static Legajo crearLegajo(){        
        System.out.println("======LEGAJO======");
        Scanner scan = new Scanner (System.in);
        System.out.println("Número de legajo: ");
        String numLegajo = scan.nextLine();
        System.out.println("Categoría: ");
        String categoria = scan.nextLine();
        System.out.println("Observaciones: ");
        String observaciones = scan.nextLine();
        int opc = -1;            
        do {
            System.out.println("Seleccione una opción: ");
            System.out.println("1. ACTIVO");
            System.out.println("2. INACTIVO");
            System.out.println("3. CANCELAR");
            if (scan.hasNextInt()) {
                opc = scan.nextInt();
            } else {
                System.out.println("Ingrese una opción válida: ");
               scan.nextLine(); 
               opc = -1;
            }                
        } while (opc < 1 || opc > 3);            
            
        Estado estado = null;
        switch (opc) {
            case 1: estado = Estado.ACTIVO; break;
            case 2: estado = Estado.INACTIVO; break;
            case 3: 
                System.out.println("Operación cancelada.");
                menu();          
        }

    // Devuelve el objeto Legajo con la información ingresada por el usuario        
        return new Legajo(0L,false,numLegajo,categoria,estado,LocalDate.now(),observaciones);
             
    }
}