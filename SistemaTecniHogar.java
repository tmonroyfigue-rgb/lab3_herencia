import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaTecniHogar {

    private final Map<String, OrdenServicio> ordenes;
    private final Map<String, Cuadrilla> cuadrillas;
    private final List<Jornada> jornadas;
    private final RegistroIngresos ingresos;
    private int contadorJornadas;

    public SistemaTecniHogar() {
        ordenes = new LinkedHashMap<>();
        cuadrillas = new LinkedHashMap<>();
        jornadas = new ArrayList<>();
        ingresos = new RegistroIngresos();
        contadorJornadas = 0;
    }

    public boolean existeOrden(String codigo) {
        return ordenes.containsKey(codigo.trim());
    }

    public Cotizacion cotizar(OrdenServicio orden) {
        return new Cotizacion(orden.categoria(), orden.calcularCosto(), orden.estimarDias());
    }

    public void registrarOrden(OrdenServicio orden) {
        if (existeOrden(orden.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una orden con el código " + orden.getCodigo() + ".");
        }
        ordenes.put(orden.getCodigo(), orden);
        ingresos.registrarAlta(orden, orden.calcularCosto());
    }

    public void cargarOrdenInicial(OrdenServicio orden) {
        ordenes.put(orden.getCodigo(), orden);
    }

    public OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(codigo.trim());
        if (orden == null) {
            throw new IllegalArgumentException("No existe la orden " + codigo.trim() + ".");
        }
        return orden;
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrillas.containsKey(cuadrilla.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una cuadrilla con el código " + cuadrilla.getCodigo() + ".");
        }
        cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla c = cuadrillas.get(codigo.trim());
        if (c == null) {
            throw new IllegalArgumentException("No existe la cuadrilla " + codigo.trim() + ".");
        }
        return c;
    }

    private boolean tieneJornadaAbierta(Cuadrilla cuadrilla) {
        for (Jornada j : jornadas) {
            if (j.getCuadrilla() == cuadrilla && j.getEstado() != EstadoJornada.CERRADA) {
                return true;
            }
        }
        return false;
    }

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        if (tieneJornadaAbierta(cuadrilla)) {
            throw new IllegalStateException("La cuadrilla " + cuadrilla.getCodigo() + " ya tiene una jornada sin cerrar.");
        }
        contadorJornadas++;
        Jornada jornada = new Jornada("J-" + contadorJornadas, cuadrilla);
        jornadas.add(jornada);
        return jornada;
    }

    public Jornada buscarJornada(String id) {
        for (Jornada j : jornadas) {
            if (j.getId().equalsIgnoreCase(id.trim())) {
                return j;
            }
        }
        throw new IllegalArgumentException("No existe la jornada " + id.trim() + ".");
    }

    public void asignarOrden(String idJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.asignarOrden(orden);
    }

    public void retirarOrden(String idJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.retirarOrden(orden);
    }

    public void iniciarJornada(String idJornada) {
        Jornada jornada = buscarJornada(idJornada);
        jornada.iniciar(ingresos);
    }

    public void registrarResultado(String idJornada, String codigoOrden, boolean completada) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.registrarResultado(orden, completada);
    }

    public void mostrarOrdenes() {
        if (ordenes.isEmpty()) {
            System.out.println("No hay órdenes registradas.");
            return;
        }
        for (OrdenServicio o : ordenes.values()) {
            System.out.println(o);
        }
    }

    public void mostrarCuadrillas() {
        if (cuadrillas.isEmpty()) {
            System.out.println("No hay cuadrillas registradas.");
            return;
        }
        for (Cuadrilla c : cuadrillas.values()) {
            String estado = tieneJornadaAbierta(c) ? "Ocupada" : "Libre";
            System.out.println(c + " | " + estado);
        }
    }

    public void mostrarJornadas() {
        if (jornadas.isEmpty()) {
            System.out.println("No hay jornadas creadas.");
            return;
        }
        for (Jornada j : jornadas) {
            System.out.println(j);
            for (OrdenServicio o : j.getOrdenes()) {
                System.out.println("     - " + o.getCodigo() + " (" + o.categoria() + ", "
                        + String.format("%.2f", o.getHorasEstimadas()) + " h, " + o.getEstado() + ")");
            }
        }
    }

    public void generarReportes() {
        System.out.println("=== ÓRDENES POR CATEGORÍA Y ESTADO ===");
        Map<String, int[]> conteo = new LinkedHashMap<>();
        for (OrdenServicio o : ordenes.values()) {
            if (!conteo.containsKey(o.categoria())) {
                conteo.put(o.categoria(), new int[EstadoOrden.values().length]);
            }
            conteo.get(o.categoria())[o.getEstado().ordinal()]++;
        }
        for (String categoria : conteo.keySet()) {
            System.out.println(categoria + ":");
            int[] cantidades = conteo.get(categoria);
            for (EstadoOrden e : EstadoOrden.values()) {
                System.out.println("     " + e + ": " + cantidades[e.ordinal()]);
            }
        }

        System.out.println();
        System.out.println("=== INGRESOS ===");
        System.out.printf("Ingresos por registro: Q%.2f%n", ingresos.getIngresosRegistro());
        System.out.printf("Ingresos por visitas adicionales: Q%.2f%n", ingresos.getIngresosVisitas());
        System.out.printf("Ingreso total: Q%.2f%n", ingresos.total());
        System.out.println("Ingreso por categoría:");
        Map<String, Double> porCategoria = ingresos.getIngresosPorCategoria();
        if (porCategoria.isEmpty()) {
            System.out.println("     (sin ingresos)");
        }
        for (String categoria : porCategoria.keySet()) {
            System.out.printf("     %s: Q%.2f%n", categoria, porCategoria.get(categoria));
        }

        System.out.println();
        System.out.println("=== JORNADAS NO CERRADAS ===");
        boolean hayAbiertas = false;
        for (Jornada j : jornadas) {
            if (j.getEstado() != EstadoJornada.CERRADA) {
                hayAbiertas = true;
                System.out.printf("%s | Cuadrilla: %s | Estado: %s | Horas asignadas: %.2f | Capacidad disponible: %.2f%n",
                        j.getId(), j.getCuadrilla().getCodigo(), j.getEstado(),
                        j.horasAsignadas(), j.capacidadDisponible());
            }
        }
        if (!hayAbiertas) {
            System.out.println("No hay jornadas abiertas.");
        }
    }
}