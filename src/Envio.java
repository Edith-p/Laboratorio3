public abstract class Envio {
    
    private String codigo; 
    private String remitente; 
    private String destinatario; 
    private double pesoReal; 
    private TipoDestino destino; 
    private EstadoEnvio estado; 
    private int intentos; 
    private double ingresoRegistro;
    private double ingresoReintentos; 
    private boolean cobroRegistrado; 
    protected static final double TARIFA_BASE_URBANA = 25; 
    protected static final double TARIFA_BASE_DEPARTAMENTAL = 40; 
    private static final double CARGO_REINTENTO = 15; 
    
    protected Envio (String codigo, String remitente, String destinatario, double pesoReal, TipoDestino destino){
        this.codigo = codigo.trim().toUpperCase();
        this.remitente = remitente.trim(); 
        this.destinatario = destinatario.trim(); 
        this.pesoReal = pesoReal; 
        this.destino = destino; 
        this.estado = EstadoEnvio.EN_BODEGA; 
        this.intentos = 0; 
        this.ingresoRegistro = 0; 
        this.cobroRegistrado = false; 
    }


    public static void validarDatosComunes(String codigo, String remitente, String destinatario, double pesoReal, TipoDestino destino){
        if (codigo == null || codigo.trim().isEmpty()){
            throw new IllegalArgumentException("Debe ingresarse un codigo -_-");

        }

        if (remitente == null || remitente.trim().isEmpty()){
            throw new IllegalArgumentException("Debe haber un remitente -_-"); 
        }

        if (destinatario == null || destinatario.trim().isEmpty()){
            throw new IllegalArgumentException("Debe haber un destinatario -_-"); 
        }

        if (destino == null){
            throw new IllegalArgumentException("Se debe seleccionar un destino valido -_-"); 

        }

    }

    public String getCodigo(){
        return codigo; 
    }

    public String getRemitente(){
        return remitente; 

    }

    public String getDestinatario(){
        return destinatario; 
    }

    public double getPesoReal(){
        return pesoReal; 

    }

    public TipoDestino getDestino(){
        return destino; 
    }

    public boolean esUrbano(){
        if (destino == TipoDestino.URBANO){
            return true; 
        }else {
            return false; 
        }
    }

    public EstadoEnvio getEstado(){
        return estado; 
    }

    public int getIntentos(){
        return intentos; 
    }

    public double getIngresoTotal(){
        return this.ingresoRegistro + this.ingresoReintentos;
    }

    public double getIngresoReintentos(){
        return ingresoRegistro; 
    }

    public abstract double calcularCosto();
    public abstract int calcularDiasEntrega();
    public abstract int getMaximoIntentos(); 
    public abstract String getCategoria(); 
    public boolean esCompatibleCon(Vehiculo vehiculo){
        return true; 

    }
    protected double tarifaBase(){
        if (esUrbano()){
            return TARIFA_BASE_URBANA; 
    
        } else {
            return TARIFA_BASE_DEPARTAMENTAL; 
        }
    }

    protected double cargoPorReintento() {
        return CARGO_REINTENTO; 
    }

    protected boolean tieneIntentosDisponibles(){
        return this.intentos < getMaximoIntentos(); 
    }


    public void registrarCobroInicial(){
        if (cobroRegistrado){
            throw new IllegalStateException("Envio cobrado anteriormente"); 
        }

        ingresoRegistro =calcularCosto(); 
        cobroRegistrado = true; 
    }
    
    public void marcarAsignado() {
        if (estado != EstadoEnvio.EN_BODEGA){
            throw new IllegalStateException("el envio no está en bodega :("); 

        }

        if (!tieneIntentosDisponibles()){
            throw new IllegalStateException("Ya no hay intentos disponibles :("); 
        }
        estado = EstadoEnvio.ASIGNADO; 
    }

    public void marcarRetirado() {
        if (estado != EstadoEnvio.ASIGNADO){
            throw new IllegalStateException ("Envio no asignado -_ -"); 

        }
        estado = EstadoEnvio.EN_BODEGA; 
    }

    public void iniciarIntento() {
        if (estado != EstadoEnvio.ASIGNADO){
            throw new IllegalStateException("El envio debe ser asignado para salir a ruta :) "); 
        }
        if (!tieneIntentosDisponibles()){
            throw new IllegalStateException("Ya no hay intentos disponibles :("); 
        }
        if (intentos > 0){
            ingresoReintentos += cargoPorReintento(); 

        }
        intentos ++;
        estado = EstadoEnvio.EN_RUTA; 

    }

    public void registrarResultado(boolean entregado){
        if (estado != EstadoEnvio.EN_RUTA){
            throw new IllegalStateException("EL envio no está en ruta :( "); 
        }

        if (entregado){
            estado = EstadoEnvio.ENTREGADO; 
        } else if (tieneIntentosDisponibles()){
            estado = EstadoEnvio.EN_BODEGA; 

        } else {
            estado = EstadoEnvio.DEVUELTO_AL_REMITENTE; 
        }
    }

    @Override
    public String toString() {
        return "Codigo: " + this.codigo
                + "\nRemitente: " + this.remitente
                + "\nDestinatario: " + this.destinatario
                + "\nPeso real: " + this.pesoReal + " kg"
                + "\nDestino: " + this.destino
                + "\nEstado: " + this.estado
                + "\nIntentos: " + this.intentos
                + "\nIngreso por registro: Q" + this.ingresoRegistro
                + "\nIngreso por reintentos: Q" + this.ingresoReintentos
                + "\nIngreso total: Q" + getIngresoTotal();
    }

}
