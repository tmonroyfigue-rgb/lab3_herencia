import java.util.ArrayList;
import java.util.List;

public class Jornada {

    private final String id;
    private final Cuadrilla cuadrilla;
    private final List<OrdenServicio> ordenes;
    private final List<OrdenServicio> conResultado;
    private EstadoJornada estado;

    public Jornada(String id, Cuadrilla cuadrilla) {
        this.id = id;
        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<>();
        this.conResultado = new ArrayList<>();
        this.estado = EstadoJornada.PREPARACION;
    }

    public void asignarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.PREPARACION) {
            throw new IllegalStateException("Solo se pueden asignar órdenes a una jornada en preparación.");
        }
        if (!orden.puedeAsignarse()) {
            throw new IllegalStateException("La orden " + orden.getCodigo()
                    + " no se puede asignar (estado: " + orden.getEstado() + " o ya pertenece a otra jornada).");
        }
        if (!orden.esCompatible(cuadrilla)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo()
                    + " no es compatible con la cuadrilla " + cuadrilla.getCodigo() + ".");
        }
        if (horasAsignadas() + orden.getHorasEstimadas() > cuadrilla.getCapacidadHoras()) {
            throw new IllegalArgumentException("La orden excede la capacidad de la cuadrilla. Disponible: "
                    + String.format("%.2f", capacidadDisponible()) + " h.");
        }
        orden.asignar(this);
        ordenes.add(orden);
    }

    public void retirarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.PREPARACION) {
            throw new IllegalStateException("Solo se pueden retirar órdenes de una jornada en preparación.");
        }
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo() + " no pertenece a esta jornada.");
        }
        orden.retirar();
        ordenes.remove(orden);
    }

    public double horasAsignadas() {
        double suma = 0;
        for (OrdenServicio o : ordenes) {
            suma += o.getHorasEstimadas();
        }
        return suma;
    }

    public double capacidadDisponible() {
        return cuadrilla.getCapacidadHoras() - horasAsignadas();
    }

    public void iniciar(RegistroIngresos registro) {
        if (estado != EstadoJornada.PREPARACION) {
            throw new IllegalStateException("La jornada no está en preparación.");
        }
        if (ordenes.isEmpty()) {
            throw new IllegalStateException("No se puede iniciar una jornada vacía.");
        }
        for (OrdenServicio o : ordenes) {
            boolean recargo = o.iniciarAtencion();
            if (recargo) {
                registro.registrarVisitaAdicional(o, 40);
            }
        }
        estado = EstadoJornada.INICIADA;
    }

    public void registrarResultado(OrdenServicio orden, boolean completada) {
        if (estado != EstadoJornada.INICIADA) {
            throw new IllegalStateException("La jornada no está iniciada.");
        }
        if (!ordenes.contains(orden)) {
            throw new IllegalArgumentException("La orden " + orden.getCodigo() + " no pertenece a esta jornada.");
        }
        if (conResultado.contains(orden)) {
            throw new IllegalStateException("La orden " + orden.getCodigo() + " ya tiene resultado registrado.");
        }
        orden.registrarResultado(completada);
        conResultado.add(orden);
        if (conResultado.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
        }
    }

    public String getId() {
        return id;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public List<OrdenServicio> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    @Override
    public String toString() {
        return String.format("%s | Cuadrilla: %s | Estado: %s | Órdenes: %d | Horas asignadas: %.2f | Capacidad disponible: %.2f",
                id, cuadrilla.getCodigo(), estado, ordenes.size(), horasAsignadas(), capacidadDisponible());
    }
}