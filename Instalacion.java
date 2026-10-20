public class Instalacion extends OrdenServicio {

    private final int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a instalar deben ser un entero positivo.");
        }
        this.puntos = puntos;
    }

    protected double calcularCostoBaseInstalacion() {
        double horasPorPuntos = puntos * 0.75;
        double horasFacturables = Math.ceil(Math.max(getHorasEstimadas(), horasPorPuntos));
        return getZona().getTarifaBase() + 90 * horasFacturables;
    }

    @Override
    public double calcularCosto() {
        return calcularCostoBaseInstalacion();
    }

    @Override
    public int estimarDias() {
        if (getZona() == Zona.METROPOLITANA) {
            return 3;
        }
        return 5;
    }

    @Override
    public int maxVisitas() {
        return 3;
    }

    @Override
    public String categoria() {
        return "Instalación";
    }

    public int getPuntos() {
        return puntos;
    }
}