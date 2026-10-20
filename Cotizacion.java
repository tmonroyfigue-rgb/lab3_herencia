public class Cotizacion {

    private final String categoria;
    private final double costo;
    private final int diasHabiles;

    public Cotizacion(String categoria, double costo, int diasHabiles) {
        this.categoria = categoria;
        this.costo = costo;
        this.diasHabiles = diasHabiles;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getCosto() {
        return costo;
    }

    public int getDiasHabiles() {
        return diasHabiles;
    }

    @Override
    public String toString() {
        return String.format("Cotización -> Categoría: %s | Costo: Q%.2f | Tiempo estimado: %d día(s) hábil(es)",
                categoria, costo, diasHabiles);
    }
}