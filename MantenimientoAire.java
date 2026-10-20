public class MantenimientoAire extends OrdenServicio {

    private final int capacidadBTU;

    public MantenimientoAire(String codigo, String cliente, String direccion,
                             double horasEstimadas, Zona zona, int capacidadBTU) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new IllegalArgumentException("El mantenimiento de aire acondicionado solo se acepta en zona metropolitana.");
        }
        if (capacidadBTU < 9000 || capacidadBTU > 60000) {
            throw new IllegalArgumentException("La capacidad debe estar entre 9,000 y 60,000 BTU.");
        }
        this.capacidadBTU = capacidadBTU;
    }

    @Override
    public double calcularCosto() {
        return getZona().getTarifaBase() + 110 * getHorasEstimadas() + 80;
    }

    @Override
    public int estimarDias() {
        return 2;
    }

    @Override
    public int maxVisitas() {
        return 1;
    }

    @Override
    public String categoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public boolean esCompatible(Cuadrilla cuadrilla) {
        return cuadrilla.tieneCertificacionRefrigeracion();
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }
}