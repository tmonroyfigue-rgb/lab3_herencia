import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroIngresos {

    private double ingresosRegistro;
    private double ingresosVisitas;
    private final Map<String, Double> ingresosPorCategoria;

    public RegistroIngresos() {
        ingresosRegistro = 0;
        ingresosVisitas = 0;
        ingresosPorCategoria = new LinkedHashMap<>();
    }

    public void registrarAlta(OrdenServicio orden, double monto) {
        ingresosRegistro += monto;
        sumarCategoria(orden.categoria(), monto);
    }

    public void registrarVisitaAdicional(OrdenServicio orden, double monto) {
        ingresosVisitas += monto;
        sumarCategoria(orden.categoria(), monto);
    }

    private void sumarCategoria(String categoria, double monto) {
        double actual = 0;
        if (ingresosPorCategoria.containsKey(categoria)) {
            actual = ingresosPorCategoria.get(categoria);
        }
        ingresosPorCategoria.put(categoria, actual + monto);
    }

    public double total() {
        return ingresosRegistro + ingresosVisitas;
    }

    public double getIngresosRegistro() {
        return ingresosRegistro;
    }

    public double getIngresosVisitas() {
        return ingresosVisitas;
    }

    public Map<String, Double> getIngresosPorCategoria() {
        return new LinkedHashMap<>(ingresosPorCategoria);
    }
}