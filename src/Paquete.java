public class Paquete extends Envio {
    private double largo; 
    private double ancho; 
    private double alto; 
    private static final double DIVISOR_VOLUMETRICO = 5000; 
    private static final double TARIFA_POR_KILO = 8; 

    public Paquete(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino destino, double largo, double ancho, double alto){
        super(codigo, remitente, destinatario, pesoReal, destino); 
        validarDimensiones(largo, ancho, alto);
        this.largo = largo; 
        this.ancho = ancho; 
        this.alto = alto; 
    }
    public static void validarDimensiones(double largo, double ancho, double alto) {
        if (!Double.isFinite(largo) || largo <= 0 ||!Double.isFinite(ancho) || ancho <= 0 || !Double.isFinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("Las dimensiones deben ser mayores que cero _-_ " );
        }
    }
    
    public double getLargo(){
        return largo;
    }

    public double getAncho(){
        return ancho; 
    }

    public double getAlto(){
        return alto; 
    }

    public double calcularPesoVolumetrico(){
        return (largo * ancho * alto) / DIVISOR_VOLUMETRICO; 
    }

    public double calcularPesoFacturable(){
        return Math.max(getPesoReal(), calcularPesoVolumetrico());
    }

    public long calcularKilosCobrados(){
        return (long) Math.ceil(calcularPesoFacturable()); 
    }

    @Override 
    public double calcularCosto(){
        return tarifaBase() + (TARIFA_POR_KILO * calcularKilosCobrados());

    }
    
    @Override 
    public int calcularDiasEntrega() {
        if (esUrbano()){
            return 2; 

        }else {
            return 4; 
        }
    }

    @Override 
    public int getMaximoIntentos(){
        return 3; 
    }

    @Override 
    public String getCategoria() {
        return "Paquete"; 
    }
    
    @Override
    public String toString() {
        return super.toString()
            + String.format("""
                
                Dimensiones:         %.2f x %.2f x %.2f cm
                Peso volumetrico:    %.2f kg
                Peso facturable:     %.2f kg
                Kilos cobrados:      %d
                """,
                largo,
                ancho,
                alto,
                calcularPesoVolumetrico(),
                calcularPesoFacturable(),
                calcularKilosCobrados()
            );
        }
}