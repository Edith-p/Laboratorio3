
import java.util.ArrayList;

public class SistemaEnvios {
    private ArrayList<Envio> envios;
    private ArrayList<Vehiculo> vehiculos;
    private ArrayList<Despacho> despachos;

    public SistemaEnvios() {
        envios = new ArrayList<>();
        vehiculos = new ArrayList<>();
        despachos = new ArrayList<>();
    }

    public void validarCodigoEnvioDisponible(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("El codigo no puede estar vacio");
        for (Envio envio : envios) {
            if (envio.getCodigo().equalsIgnoreCase(codigo.trim())) throw new IllegalArgumentException("El codigo del envio ya existe");
        }
    }

    public void validarPlacaDisponible(String placa) {
        if (placa == null || placa.trim().isEmpty()) throw new IllegalArgumentException("La placa no puede estar vacia");
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getPlaca().equalsIgnoreCase(placa.trim())) throw new IllegalArgumentException("La placa ya esta registrada");
        }
    }

    public void validarCreacionDespacho(String codigo, String placa) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("El codigo del despacho no puede estar vacio");
        for (Despacho despacho : despachos) {
            if (despacho.getCodigo().equalsIgnoreCase(codigo.trim())) throw new IllegalArgumentException("El codigo del despacho ya existe");
        }
        Vehiculo vehiculo = buscarVehiculo(placa);
        if (!vehiculoEstaLibre(vehiculo)) throw new IllegalStateException("El vehiculo tiene un despacho activo");
    }

    public void validarAsignacionEnvio(String codigoDespacho, String codigoEnvio) {
        Despacho despacho = buscarDespacho(codigoDespacho);
        Envio envio = buscarEnvio(codigoEnvio);
        despacho.validarAsignacion(envio);
    }

    public void validarRetiroEnvio(String codigoDespacho, String codigoEnvio) {
        Despacho despacho = buscarDespacho(codigoDespacho);
        despacho.validarRetiro(codigoEnvio);
    }

    public void validarSalidaDespacho(String codigoDespacho) {
        Despacho despacho = buscarDespacho(codigoDespacho);
        despacho.validarSalida();
    }

    public void validarResultadoEntrega(String codigoDespacho, String codigoEnvio) {
        Despacho despacho = buscarDespacho(codigoDespacho);
        despacho.validarResultado(codigoEnvio);
    }

    public void registrarEnvio(Envio envio) {
        if (envio == null) throw new IllegalArgumentException("El envio no puede ser nulo");
        validarCodigoEnvioDisponible(envio.getCodigo());
        envio.registrarCobroInicial();
        envios.add(envio);
    }

    public void precargarEnvio(Envio envio) {
        if (envio == null) throw new IllegalArgumentException("El envio no puede ser nulo");
        validarCodigoEnvioDisponible(envio.getCodigo());
        envios.add(envio);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) throw new IllegalArgumentException("El vehiculo no puede ser nulo");
        validarPlacaDisponible(vehiculo.getPlaca());
        vehiculos.add(vehiculo);
    }

    public Despacho crearDespacho(String codigo, String placa) {
        validarCreacionDespacho(codigo, placa);
        Vehiculo vehiculo = buscarVehiculo(placa);
        Despacho despacho = new Despacho(codigo, vehiculo);
        despachos.add(despacho);
        return despacho;
    }

    public void asignarEnvio(String codigoDespacho, String codigoEnvio) {
        validarAsignacionEnvio(codigoDespacho, codigoEnvio);
        Despacho despacho = buscarDespacho(codigoDespacho);
        Envio envio = buscarEnvio(codigoEnvio);
        despacho.agregarEnvio(envio);
    }

    public void retirarEnvio(String codigoDespacho, String codigoEnvio) {
        validarRetiroEnvio(codigoDespacho, codigoEnvio);
        buscarDespacho(codigoDespacho).retirarEnvio(codigoEnvio);
    }

    public void hacerSalirDespacho(String codigoDespacho) {
        validarSalidaDespacho(codigoDespacho);
        buscarDespacho(codigoDespacho).salir();
    }

    public void registrarResultadoEntrega(String codigoDespacho, String codigoEnvio, boolean entregado) {
        validarResultadoEntrega(codigoDespacho, codigoEnvio);
        buscarDespacho(codigoDespacho).registrarResultado(codigoEnvio, entregado);
    }

    public ArrayList<Envio> consultarEnvios() {
        return new ArrayList<>(envios);
    }

    public ArrayList<Vehiculo> consultarVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public boolean vehiculoEstaLibre(Vehiculo vehiculo) {
        if (vehiculo == null) throw new IllegalArgumentException("El vehiculo no puede ser nulo");
        for (Despacho despacho : despachos) {
            if (despacho.getVehiculo().getPlaca().equalsIgnoreCase(vehiculo.getPlaca()) && !despacho.estaCerrado()) return false;
        }
        return true;
    }

    public String contarPorCategoriaYEstado() {
        StringBuilder reporte = new StringBuilder("""
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            ENVIOS POR CATEGORIA Y ESTADO
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            """);

        ArrayList<String> categorias = new ArrayList<>();

        for (Envio envio : envios) {
            if (!categorias.contains(envio.getCategoria())) categorias.add(envio.getCategoria());
        }

        for (String categoria : categorias) {
            reporte.append(String.format("%nCategoria: %s%n", categoria));

            for (EstadoEnvio estado : EstadoEnvio.values()) {
                int cantidad = 0;

                for (Envio envio : envios) {
                    if (envio.getCategoria().equals(categoria) && envio.getEstado() == estado) cantidad++;
                }

                reporte.append(String.format("  %s: %d%n", estado, cantidad));
            }
        }

        return reporte.toString();
    }

    public double ingresoPorRegistro() {
        double total = 0;
        for (Envio envio : envios) total += envio.getIngresoRegistro();
        return total;
    }

    public double ingresoPorReintentos() {
        double total = 0;
        for (Envio envio : envios) total += envio.getIngresoReintentos();
        return total;
    }

    public double ingresoTotal() {
        return ingresoPorRegistro() + ingresoPorReintentos();
    }

    public String ingresoPorCategoria() {
        StringBuilder reporte = new StringBuilder("""
            +-+-+-+-+-+-+-+-+-+-+-+-+-+
            INGRESOS POR CATEGORIA
            +-+-+-+-+-+-+-+-+-+-+-+-+-+
            """);

        ArrayList<String> categorias = new ArrayList<>();

        for (Envio envio : envios) {
            if (!categorias.contains(envio.getCategoria())) categorias.add(envio.getCategoria());
        }

        for (String categoria : categorias) {
            double total = 0;

            for (Envio envio : envios) {
                if (envio.getCategoria().equals(categoria)) total += envio.getIngresoTotal();
            }

            reporte.append(String.format("%s: Q%.2f%n", categoria, total));
        }

        return reporte.toString();
    }

    public ArrayList<Despacho> despachosNoCerrados() {
        ArrayList<Despacho> activos = new ArrayList<>();
        for (Despacho despacho : despachos) {
            if (!despacho.estaCerrado()) activos.add(despacho);
        }
        return activos;
    }

    private Envio buscarEnvio(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("El codigo del envio no puede estar vacio -_-");
        for (Envio envio : envios) {
            if (envio.getCodigo().equalsIgnoreCase(codigo.trim())) return envio;
        }
        throw new IllegalArgumentException("El envio no existe :/");
    }

    private Vehiculo buscarVehiculo(String placa) {
        if (placa == null || placa.trim().isEmpty()) throw new IllegalArgumentException("La placa no puede estar vacia -_-");
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getPlaca().equalsIgnoreCase(placa.trim())) return vehiculo;
        }
        throw new IllegalArgumentException("El vehiculo no existe :/");
    }

    private Despacho buscarDespacho(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("El codigo del despacho no puede estar vacio -_-");
        for (Despacho despacho : despachos) {
            if (despacho.getCodigo().equalsIgnoreCase(codigo.trim())) return despacho;
        }
        throw new IllegalArgumentException("El despacho no existe :(");
    }


    public void validarCodigoDespachoDisponible(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo no puede estar vacio -_-");
        }

        for (Despacho despacho : despachos) {
            if (despacho.getCodigo().equalsIgnoreCase(codigo.trim())) {
                throw new IllegalArgumentException("El codigo del despacho ya existe :/");
            }
        }
    }

}