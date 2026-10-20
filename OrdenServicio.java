public abstract class OrdenServicio {

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;
    private Jornada jornada;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        if (horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser mayores que cero.");
        }
        if (zona == null) {
            throw new IllegalArgumentException("Debe indicar una zona.");
        }
        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
        this.jornada = null;
    }

    public abstract double calcularCosto();

    public abstract int estimarDias();

    public abstract int maxVisitas();

    public abstract String categoria();

    public boolean esCompatible(Cuadrilla cuadrilla) {
        return true;
    }

    public boolean puedeAsignarse() {
        return estado == EstadoOrden.PENDIENTE && jornada == null;
    }

    public void asignar(Jornada nuevaJornada) {
        if (!puedeAsignarse()) {
            throw new IllegalStateException("La orden " + codigo + " no puede asignarse (estado: " + estado + ").");
        }
        this.jornada = nuevaJornada;
        this.estado = EstadoOrden.ASIGNADA;
    }

    public void retirar() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new IllegalStateException("Solo se puede retirar una orden asignada (estado: " + estado + ").");
        }
        this.jornada = null;
        this.estado = EstadoOrden.PENDIENTE;
    }

    public boolean iniciarAtencion() {
        if (estado != EstadoOrden.ASIGNADA) {
            throw new IllegalStateException("La orden " + codigo + " no está asignada (estado: " + estado + ").");
        }
        boolean recargo = visitasRealizadas >= 1;
        this.estado = EstadoOrden.EN_ATENCION;
        return recargo;
    }

    public void registrarResultado(boolean completada) {
        if (estado != EstadoOrden.EN_ATENCION) {
            throw new IllegalStateException("La orden " + codigo + " no está en atención (estado: " + estado + ").");
        }
        visitasRealizadas++;
        jornada = null;
        if (completada) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= maxVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Cliente: %s | Dir: %s | Zona: %s | Horas: %.2f | Estado: %s | Visitas: %d/%d | Costo: Q%.2f",
                codigo, categoria(), cliente, direccion, zona, horasEstimadas,
                estado, visitasRealizadas, maxVisitas(), calcularCosto());
    }
}