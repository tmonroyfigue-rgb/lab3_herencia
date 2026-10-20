public class InstalacionElectricaCertificada extends Instalacion {

    private final double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion,
                                           double horasEstimadas, Zona zona, int puntos,
                                           double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        if (valorMateriales < 0) {
            throw new IllegalArgumentException("El valor de los materiales no puede ser negativo.");
        }
        this.valorMateriales = valorMateriales;
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + 120 + 0.05 * valorMateriales;
    }

    @Override
    public int estimarDias() {
        return super.estimarDias() + 2;
    }

    @Override
    public int maxVisitas() {
        return 2;
    }

    @Override
    public String categoria() {
        return "Instalación eléctrica certificada";
    }

    public double getValorMateriales() {
        return valorMateriales;
    }
}