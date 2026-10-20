public class Cuadrilla {

    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de la cuadrilla no puede estar vacío.");
        }
        if (capacidadHoras <= 0) {
            throw new IllegalArgumentException("La capacidad de la cuadrilla debe ser mayor que cero.");
        }
        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean tieneCertificacionRefrigeracion() {
        return certificadaRefrigeracion;
    }

    @Override
    public String toString() {
        String cert = certificadaRefrigeracion ? "Sí" : "No";
        return String.format("%s | Capacidad: %.2f h | Certificada en refrigeración: %s",
                codigo, capacidadHoras, cert);
    }
}