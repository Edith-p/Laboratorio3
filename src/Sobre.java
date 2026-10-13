public class Sobre extends Envio{
    private static final double PESO_MAXIMO = 0.5; 
    private static final double TARIFA_URBANA = 20; 
    private static final double TARIFA_DEPARTAMENTAL = 35; 
    

    public Sobre(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino destino){
        super(codigo, remitente, destinatario, pesoReal, destino);
        validarPeso(pesoReal);
    }

    public static void validarPeso(double pesoReal){
        if(pesoReal > PESO_MAXIMO){
            throw new IllegalArgumentException("El peso no debe superar los 0.5kg"); 
        }
    }

    @Override 
    public int calcularDiasEntrega(){
        if (esUrbano()){
            return 1; 
        }else{
            return 2; 
        }
    }

    @Override 
    public int getMaximoIntentos(){
        return 3; 

    }

    @Override 
    public String getCategoria(){
        return "Sobre" ;
    }

    @Override 
    public double calcularCosto() {
        if (esUrbano()){
            return TARIFA_URBANA; 
        }else{
            return TARIFA_DEPARTAMENTAL; 
        }
    }
}
