public class Diagnostico extends OrdenServicio {

    public Diagnostico(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > 1.5) {
            throw new IllegalArgumentException("Un diagnóstico no puede superar 1.5 horas.");
        }
    }

    @Override
    public double calcularCosto() {
        if (getZona() == Zona.METROPOLITANA) {
            return 150;
        }
        return 225;
    }

    @Override
    public int estimarDias() {
        if (getZona() == Zona.METROPOLITANA) {
            return 1;
        }
        return 2;
    }

    @Override
    public int maxVisitas() {
        return 3;
    }

    @Override
    public String categoria() {
        return "Diagnóstico";
    }
}