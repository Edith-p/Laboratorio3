import java.util.ArrayList;

public class Despacho {
    private String codigo;
    private Vehiculo vehiculo;
    private EstadoDespacho estado;
    private ArrayList<Envio> envios;
    private ArrayList<ResultadoEntrega> resultados;

    public Despacho(String codigo, Vehiculo vehiculo) {
        validarDatosDespacho(codigo, vehiculo);
        this.codigo = codigo.trim().toUpperCase();
        this.vehiculo = vehiculo;
        this.estado = EstadoDespacho.EN_PREPARACION;
        this.envios = new ArrayList<>();
        this.resultados = new ArrayList<>();
    }

    public static void validarDatosDespacho(String codigo, Vehiculo vehiculo) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("El codigo no puede estar vacio");
        if (vehiculo == null) throw new IllegalArgumentException("El vehiculo no puede ser nulo");
    }

    public String getCodigo() { return codigo; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public EstadoDespacho getEstado() { return estado; }
    public ArrayList<Envio> getEnvios() { return new ArrayList<>(envios); }

    public double calcularPesoCargado() {
        double pesoTotal = 0;
        for (Envio envio : envios) pesoTotal += envio.getPesoReal();
        return pesoTotal;
    }

    public double calcularCapacidadDisponible() {
        return vehiculo.getCapacidadMaxima() - calcularPesoCargado();
    }

    public void validarAsignacion(Envio envio) {
        if (estado != EstadoDespacho.EN_PREPARACION) throw new IllegalStateException("El despacho no esta en preparacion");
        if (envio == null) throw new IllegalArgumentException("El envio no puede ser nulo");
        if (envio.getEstado() != EstadoEnvio.EN_BODEGA) throw new IllegalStateException("El envio no esta en bodega");
        if (!envio.tieneIntentosDisponibles()) throw new IllegalStateException("El envio ya no tiene intentos disponibles");
        if (envios.contains(envio)) throw new IllegalArgumentException("El envio ya pertenece al despacho");
        if (envio.getPesoReal() > calcularCapacidadDisponible()) throw new IllegalArgumentException("El envio supera la capacidad disponible");
        if (!envio.esCompatibleCon(vehiculo)) throw new IllegalArgumentException("El envio no es compatible con el vehiculo");
    }

    public void agregarEnvio(Envio envio) {
        validarAsignacion(envio);
        envio.marcarAsignado();
        envios.add(envio);
        resultados.add(ResultadoEntrega.PENDIENTE);
    }

    private int buscarIndiceEnvio(String codigoEnvio) {
        if (codigoEnvio == null || codigoEnvio.trim().isEmpty()) throw new IllegalArgumentException("El codigo del envio no puede estar vacio");
        for (int i = 0; i < envios.size(); i++) {
            if (envios.get(i).getCodigo().equalsIgnoreCase(codigoEnvio.trim())) return i;
        }
        throw new IllegalArgumentException("El envio no pertenece al despacho");
    }

    public void validarRetiro(String codigoEnvio) {
        if (estado != EstadoDespacho.EN_PREPARACION) throw new IllegalStateException("Solo se puede retirar en preparacion");
        int indice = buscarIndiceEnvio(codigoEnvio);
        if (envios.get(indice).getEstado() != EstadoEnvio.ASIGNADO) throw new IllegalStateException("El envio no esta asignado");
    }

    public void retirarEnvio(String codigoEnvio) {
        validarRetiro(codigoEnvio);
        int indice = buscarIndiceEnvio(codigoEnvio);
        envios.get(indice).marcarRetirado();
        envios.remove(indice);
        resultados.remove(indice);
    }

    public void validarSalida() {
        if (estado != EstadoDespacho.EN_PREPARACION) throw new IllegalStateException("El despacho no esta en preparacion");
        if (envios.isEmpty()) throw new IllegalStateException("No se puede enviar un despacho vacio");
        for (Envio envio : envios) {
            if (envio.getEstado() != EstadoEnvio.ASIGNADO || !envio.tieneIntentosDisponibles()) {
                throw new IllegalStateException("Hay un envio que no puede salir");
            }
        }
    }

    public void salir() {
        validarSalida();
        for (Envio envio : envios) envio.iniciarIntento();
        estado = EstadoDespacho.EN_RUTA;
    }

    public void validarResultado(String codigoEnvio) {
        if (estado != EstadoDespacho.EN_RUTA) throw new IllegalStateException("El despacho no esta en ruta");
        int indice = buscarIndiceEnvio(codigoEnvio);
        if (resultados.get(indice) != ResultadoEntrega.PENDIENTE) throw new IllegalStateException("El envio ya tiene un resultado");
        if (envios.get(indice).getEstado() != EstadoEnvio.EN_RUTA) throw new IllegalStateException("El envio no esta en ruta");
    }

    public void registrarResultado(String codigoEnvio, boolean entregado) {
        validarResultado(codigoEnvio);
        int indice = buscarIndiceEnvio(codigoEnvio);
        envios.get(indice).registrarResultado(entregado);
        resultados.set(indice, entregado ? ResultadoEntrega.ENTREGADO : ResultadoEntrega.FALLIDO);
        if (todosConResultado()) cerrar();
    }

    public boolean estaCerrado() { return estado == EstadoDespacho.CERRADO; }

    private boolean todosConResultado() {
        for (ResultadoEntrega resultado : resultados) {
            if (resultado == ResultadoEntrega.PENDIENTE) return false;
        }
        return true;
    }

    private void cerrar() { estado = EstadoDespacho.CERRADO; }

    @Override
    public String toString() {
        return String.format("""
            +-+-+-+-+-+-+-+-+-+-+-+-
            Despacho: %s
            Vehiculo: %s
            Estado: %s
            Envios: %d
            Peso cargado: %.2f kg
            Capacidad disponible: %.2f kg
            +-+-+-+-+-+-+-+-+-+-+-+-
            """,
            codigo, vehiculo.getPlaca(), estado, envios.size(),
            calcularPesoCargado(), calcularCapacidadDisponible()
        );
    }
}