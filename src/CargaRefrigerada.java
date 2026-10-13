public class CargaRefrigerada extends Envio{
    private double temperaturaRequerida; 
    private static final double TEMPERATURA_MINIMA = -25; 
    private static final double TEMPERATURA_MAXIMA = 8; 
    private static final double TARIFA_POR_KG = 12;
    private static final double CARGO_CADENA_FRIO = 60;

    public CargaRefrigerada(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino destino, double temperaturaRequerida){
        super(codigo, remitente, destinatario, pesoReal, destino);
        validarRefrigeracion(destino, temperaturaRequerida);
        this.temperaturaRequerida = temperaturaRequerida; 
    }

    public static void validarRefrigeracion(TipoDestino destino, double temperaturaRequerida){
        if (destino != TipoDestino.URBANO){
            throw new IllegalArgumentException("Este tipo de carga es solo para destino urbano :( "); 
        }

        if (!Double.isFinite(temperaturaRequerida)|| temperaturaRequerida < TEMPERATURA_MINIMA || temperaturaRequerida > TEMPERATURA_MAXIMA){
            throw new IllegalArgumentException("La temperatura debe estar entre -25°C y 8°C :/ "); 

        }
    }

    public double getTemperaturaRequerida() {
        return this.temperaturaRequerida; 
    }

    @Override 
    public double calcularCosto() {
        return tarifaBase() + (TARIFA_POR_KG * getPesoReal()) +  CARGO_CADENA_FRIO;

    }

    @Override 
    public int calcularDiasEntrega() {
        return 1; 
    }

    @Override 
    public int getMaximoIntentos() {
        return 1; 
    }

    @Override 
    public String getCategoria() {
        return "Carga refrigerada"; 
    }

    @Override 
    public boolean esCompatibleCon(Vehiculo vehiculo){
        return vehiculo != null && vehiculo.tieneRefrigeracion(); 
    }


    @Override
    public String toString() {
        return super.toString()
                + "\nTemperatura requerida: "
                + temperaturaRequerida + " °C";
    }
}

