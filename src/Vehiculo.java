public class Vehiculo {
    // Se le pone final para que no se pueda cambiar su valor después de asignado una primera vez
    private final String placa;
    private double capacidadMaxima;
    private boolean tieneRefrigeracion;

    public Vehiculo(String placa, double capacidadMaxima, boolean tieneRefrigeracion) {
        validarDatosVehiculo(placa, capacidadMaxima);
        this.placa = placa.trim().toUpperCase();
        this.capacidadMaxima = capacidadMaxima;
        this.tieneRefrigeracion = tieneRefrigeracion;
    }

    public static void validarDatosVehiculo(String placa, double capacidadMaxima) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacia");
        }

        if (!Double.isFinite(capacidadMaxima) || capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
    }

    public String getPlaca() {
        return placa;
    }

    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean tieneRefrigeracion() {
        return tieneRefrigeracion;
    }

    @Override
    public String toString() {
        return String.format("""
            +-+-+-+-+-+-+-+-+-+-+-
            Placa: %s 
            Capacidad: %.2f kg
            Refrigeracion: %s    
            +-+-+-+-+-+-+-+-+-+-+-    
            """,
            placa, capacidadMaxima, tieneRefrigeracion ? "Si" : "No"
        );
    }
}