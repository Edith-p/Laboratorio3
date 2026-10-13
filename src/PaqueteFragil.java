public class PaqueteFragil extends Paquete{
    private double valorDeclarado; 
    private static final double CARGO_EMBALAJE = 15; 
    private static final double PORCENTAJE_SEGURO = 0.03; 


    public PaqueteFragil(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino destino, double largo, double ancho, double alto, double valorDeclarado){
        super(codigo, remitente, destinatario, pesoReal, destino, largo, ancho, alto); 

        validarValorDeclarado(valorDeclarado); 
        this.valorDeclarado = valorDeclarado; 
    }

    public static void validarValorDeclarado (double valorDeclarado){
        if (!Double.isFinite(valorDeclarado) || valorDeclarado <= 0){
            throw new IllegalArgumentException( "El valor declarado debe ser mayor a 0 -_-"); 

        }

    }

    public double getValorDeclarado(){
        return valorDeclarado; 
    }

    public double calcularSeguro(){
        return valorDeclarado * PORCENTAJE_SEGURO; 
    }

    @Override 
    public double calcularCosto(){
        return super.calcularCosto() + CARGO_EMBALAJE + calcularSeguro();
    }

    @Override
    public int calcularDiasEntrega() {
        return super.calcularDiasEntrega() +1; 
    }

    @Override 
    public int getMaximoIntentos() {
        return 2;
    }

    @Override 
    public String getCategoria(){
        return "Paquete fragil"; 
    }

    @Override
    public String toString() {
        return super.toString()
            + String.format("""
                Valor declarado:     Q%.2f
                Costo del seguro:    Q%.2f
                """,
                valorDeclarado,
                calcularSeguro()
            );
    }
}
