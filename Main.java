import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final SistemaTecniHogar sistema = new SistemaTecniHogar();

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        cargarDatosIniciales();

        int opcion = -1;
        do {
            mostrarMenu();
            try {
                opcion = leerEntero("Opción: ");
                ejecutarOpcion(opcion);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);

        System.out.println("¡Hasta luego!");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== TECNIHOGAR =====");
        System.out.println("1. Cotizar / registrar orden");
        System.out.println("2. Registrar cuadrilla");
        System.out.println("3. Crear jornada");
        System.out.println("4. Asignar orden a jornada");
        System.out.println("5. Retirar orden de jornada");
        System.out.println("6. Iniciar jornada");
        System.out.println("7. Registrar resultado de visita");
        System.out.println("8. Consultar órdenes");
        System.out.println("9. Consultar cuadrillas");
        System.out.println("10. Consultar jornadas");
        System.out.println("11. Reportes");
        System.out.println("0. Salir");
    }

    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                cotizarYRegistrar();
                break;
            case 2:
                registrarCuadrilla();
                break;
            case 3:
                crearJornada();
                break;
            case 4:
                asignarOrden();
                break;
            case 5:
                retirarOrden();
                break;
            case 6:
                iniciarJornada();
                break;
            case 7:
                registrarResultado();
                break;
            case 8:
                sistema.mostrarOrdenes();
                break;
            case 9:
                sistema.mostrarCuadrillas();
                break;
            case 10:
                sistema.mostrarJornadas();
                break;
            case 11:
                sistema.generarReportes();
                break;
            case 0:
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void cotizarYRegistrar() {
        String codigo = leerTexto("Código de la orden: ");
        if (codigo.isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        if (sistema.existeOrden(codigo)) {
            throw new IllegalArgumentException("Ya existe una orden con el código " + codigo + ".");
        }

        OrdenServicio orden = leerOrden(codigo);
        Cotizacion cotizacion = sistema.cotizar(orden);
        System.out.println(cotizacion);

        String respuesta = leerTexto("¿El cliente acepta? (s/n): ");
        if (respuesta.equalsIgnoreCase("s")) {
            sistema.registrarOrden(orden);
            System.out.printf("Orden registrada y cobrada: Q%.2f. Estado: PENDIENTE.%n", orden.calcularCosto());
        } else {
            System.out.println("Cotización descartada. No se registró nada.");
        }
    }

    private static OrdenServicio leerOrden(String codigo) {
        System.out.println("Categoría:");
        System.out.println("  1. Diagnóstico");
        System.out.println("  2. Instalación");
        System.out.println("  3. Instalación eléctrica certificada");
        System.out.println("  4. Mantenimiento de aire acondicionado");
        int categoria = leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) {
            throw new IllegalArgumentException("Categoría no válida.");
        }

        String cliente = leerTexto("Nombre del cliente: ");
        String direccion = leerTexto("Dirección: ");
        Zona zona = leerZona();
        double horas = leerDouble("Horas estimadas: ");

        switch (categoria) {
            case 1:
                return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2:
                int puntos = leerEntero("Cantidad de puntos: ");
                return new Instalacion(codigo, cliente, direccion, horas, zona, puntos);
            case 3:
                int puntosCert = leerEntero("Cantidad de puntos: ");
                double materiales = leerDouble("Valor declarado de materiales (Q): ");
                return new InstalacionElectricaCertificada(codigo, cliente, direccion, horas, zona,
                        puntosCert, materiales);
            default:
                int btu = leerEntero("Capacidad del equipo (BTU): ");
                return new MantenimientoAire(codigo, cliente, direccion, horas, zona, btu);
        }
    }

    private static void registrarCuadrilla() {
        String codigo = leerTexto("Código de la cuadrilla: ");
        double capacidad = leerDouble("Capacidad máxima de horas por jornada: ");
        String cert = leerTexto("¿Certificada en refrigeración? (s/n): ");
        boolean certificada = cert.equalsIgnoreCase("s");
        if (!certificada && !cert.equalsIgnoreCase("n")) {
            throw new IllegalArgumentException("Responda 's' o 'n'.");
        }
        Cuadrilla cuadrilla = new Cuadrilla(codigo, capacidad, certificada);
        sistema.registrarCuadrilla(cuadrilla);
        System.out.println("Cuadrilla registrada.");
    }

    private static void crearJornada() {
        String codigoCuadrilla = leerTexto("Código de la cuadrilla: ");
        Jornada jornada = sistema.crearJornada(codigoCuadrilla);
        System.out.println("Jornada creada: " + jornada.getId() + " (en preparación).");
    }

    private static void asignarOrden() {
        String idJornada = leerTexto("ID de la jornada: ");
        String codigoOrden = leerTexto("Código de la orden: ");
        sistema.asignarOrden(idJornada, codigoOrden);
        System.out.println("Orden asignada.");
    }

    private static void retirarOrden() {
        String idJornada = leerTexto("ID de la jornada: ");
        String codigoOrden = leerTexto("Código de la orden: ");
        sistema.retirarOrden(idJornada, codigoOrden);
        System.out.println("Orden retirada. Vuelve a estar pendiente.");
    }

    private static void iniciarJornada() {
        String idJornada = leerTexto("ID de la jornada: ");
        sistema.iniciarJornada(idJornada);
        System.out.println("Jornada iniciada. Las órdenes están en atención.");
    }

    private static void registrarResultado() {
        String idJornada = leerTexto("ID de la jornada: ");
        String codigoOrden = leerTexto("Código de la orden: ");
        System.out.println("Resultado: 1. Completada  2. Visita fallida");
        int resultado = leerEntero("Resultado: ");
        if (resultado != 1 && resultado != 2) {
            throw new IllegalArgumentException("Resultado no válido.");
        }
        sistema.registrarResultado(idJornada, codigoOrden, resultado == 1);
        OrdenServicio orden = sistema.buscarOrden(codigoOrden);
        System.out.println("Resultado registrado. Estado de la orden: " + orden.getEstado());
        if (sistema.buscarJornada(idJornada).getEstado() == EstadoJornada.CERRADA) {
            System.out.println("La jornada quedó CERRADA y la cuadrilla está libre.");
        }
    }

    private static Zona leerZona() {
        System.out.println("Zona: 1. Metropolitana  2. Periférica");
        int z = leerEntero("Zona: ");
        if (z == 1) {
            return Zona.METROPOLITANA;
        }
        if (z == 2) {
            return Zona.PERIFERICA;
        }
        throw new IllegalArgumentException("Zona no válida.");
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un número entero válido.");
        }
    }

    private static double leerDouble(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            double valor = Double.parseDouble(texto);
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                throw new IllegalArgumentException("Debe ingresar un número válido.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un número válido.");
        }
    }

    private static void cargarDatosIniciales() {
        sistema.cargarOrdenInicial(new Diagnostico("D-001", "Ana López", "Zona 10, Guatemala", 1.0, Zona.METROPOLITANA));
        sistema.cargarOrdenInicial(new Diagnostico("D-002", "Luis Pérez", "Villa Nueva", 1.5, Zona.PERIFERICA));
        sistema.cargarOrdenInicial(new Instalacion("I-001", "María Gómez", "Zona 15, Guatemala", 2.5, Zona.METROPOLITANA, 5));
        sistema.cargarOrdenInicial(new Instalacion("I-002", "Carlos Ruiz", "Mixco", 2.0, Zona.PERIFERICA, 4));
        sistema.cargarOrdenInicial(new InstalacionElectricaCertificada("E-001", "Sofía Díaz", "Zona 11, Guatemala",
                2.5, Zona.METROPOLITANA, 5, 2000));
        sistema.cargarOrdenInicial(new InstalacionElectricaCertificada("E-002", "Pedro Castillo", "San Miguel Petapa",
                3.0, Zona.PERIFERICA, 6, 1500));
        sistema.cargarOrdenInicial(new MantenimientoAire("M-001", "Elena Morales", "Zona 14, Guatemala",
                2.0, Zona.METROPOLITANA, 18000));
        sistema.cargarOrdenInicial(new MantenimientoAire("M-002", "Jorge Herrera", "Zona 1, Guatemala",
                1.5, Zona.METROPOLITANA, 24000));

        sistema.registrarCuadrilla(new Cuadrilla("C-01", 8.0, true));
        sistema.registrarCuadrilla(new Cuadrilla("C-02", 6.0, false));
        sistema.registrarCuadrilla(new Cuadrilla("C-03", 3.0, false));
    }
}