
public class Controlador {
    private SistemaEnvios sistema;
    private Vista vista;

    public Controlador(SistemaEnvios sistema, Vista vista) {
        this.sistema = sistema;
        this.vista = vista;
        cargarDatosIniciales();
    }

    public void iniciar() {
        int opcion;

        do {
            opcion = vista.mostrarMenu();
            if (opcion != 0) procesarOpcion(opcion);
        } while (opcion != 0);

        vista.mostrarMensaje("Gracias por utilizar EnviaGT");
    }

    private void cargarDatosIniciales() {
        sistema.precargarEnvio(new Sobre("S001", "Ana", "Luis", 0.3, TipoDestino.URBANO));
        sistema.precargarEnvio(new Sobre("S002", "Maria", "Pedro", 0.4, TipoDestino.DEPARTAMENTAL));

        sistema.precargarEnvio(new Paquete("P001", "Carlos", "Sofia", 3.2, TipoDestino.URBANO, 40, 30, 20));
        sistema.precargarEnvio(new Paquete("P002", "Jose", "Elena", 2.5, TipoDestino.DEPARTAMENTAL, 20, 20, 20));

        sistema.precargarEnvio(new PaqueteFragil("F001", "Andrea", "Mario", 3.2, TipoDestino.URBANO, 40, 30, 20, 1200));
        sistema.precargarEnvio(new PaqueteFragil("F002", "Lucia", "Diego", 2, TipoDestino.DEPARTAMENTAL, 25, 20, 15, 500));

        sistema.precargarEnvio(new CargaRefrigerada("R001", "Pedro", "Ana", 5, TipoDestino.URBANO, -10));
        sistema.precargarEnvio(new CargaRefrigerada("R002", "Luis", "Maria", 3, TipoDestino.URBANO, 4));

        sistema.registrarVehiculo(new Vehiculo("P001ABC", 100, false));
        sistema.registrarVehiculo(new Vehiculo("P002DEF", 200, true));
        sistema.registrarVehiculo(new Vehiculo("P003GHI", 150, false));
    }

    private void procesarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> cotizarYRegistrarEnvio();
                case 2 -> registrarVehiculo();
                case 3 -> crearDespacho();
                case 4 -> asignarEnvio();
                case 5 -> retirarEnvio();
                case 6 -> hacerSalirDespacho();
                case 7 -> registrarResultado();
                case 8 -> consultarEnvios();
                case 9 -> consultarVehiculos();
                case 10 -> generarReporteEnvios();
                case 11 -> generarReporteIngresos();
                case 12 -> generarReporteDespachos();
                default -> vista.mostrarError("Opcion no valida");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void cotizarYRegistrarEnvio() {
        Envio envio = leerEnvio();
        vista.mostrarCotizacion(envio);

        if (vista.leerSiNo("Desea registrar el envio")) {
            sistema.registrarEnvio(envio);
            vista.mostrarMensaje("Envio registrado correctamente");
        } else {
            vista.mostrarMensaje("Cotizacion finalizada sin registrar el envio");
        }
    }

    private Envio leerEnvio() {
        vista.mostrarMensaje("""
            1. Sobre
            2. Paquete
            3. Paquete fragil
            4. Carga refrigerada
            """);

        int categoria = vista.leerEntero("Seleccione la categoria");
        if (categoria < 1 || categoria > 4) throw new IllegalArgumentException("Categoria no valida");

        String codigo = vista.leerTexto("Codigo del envio");
        String remitente = vista.leerTexto("Nombre del remitente");
        String destinatario = vista.leerTexto("Nombre del destinatario");
        double pesoReal = vista.leerDecimal("Peso real en kg");
        TipoDestino destino = vista.leerDestino();

        Envio.validarDatosComunes(codigo, remitente, destinatario, pesoReal, destino);
        sistema.validarCodigoEnvioDisponible(codigo);

        switch (categoria) {
            case 1 -> {
                Sobre.validarPeso(pesoReal);
                return new Sobre(codigo, remitente, destinatario, pesoReal, destino);
            }
            case 2 -> {
                double largo = vista.leerDecimal("Largo en cm");
                double ancho = vista.leerDecimal("Ancho en cm");
                double alto = vista.leerDecimal("Alto en cm");
                Paquete.validarDimensiones(largo, ancho, alto);
                return new Paquete(codigo, remitente, destinatario, pesoReal, destino, largo, ancho, alto);
            }
            case 3 -> {
                double largo = vista.leerDecimal("Largo en cm");
                double ancho = vista.leerDecimal("Ancho en cm");
                double alto = vista.leerDecimal("Alto en cm");
                double valor = vista.leerDecimal("Valor declarado en Q");
                Paquete.validarDimensiones(largo, ancho, alto);
                PaqueteFragil.validarValorDeclarado(valor);
                return new PaqueteFragil(codigo, remitente, destinatario, pesoReal, destino, largo, ancho, alto, valor);
            }
            case 4 -> {
                double temperatura = vista.leerDecimal("Temperatura requerida");
                CargaRefrigerada.validarRefrigeracion(destino, temperatura);
                return new CargaRefrigerada(codigo, remitente, destinatario, pesoReal, destino, temperatura);
            }
            default -> throw new IllegalArgumentException("Categoria no valida");
        }
    }

    private void registrarVehiculo() {
        String placa = vista.leerTexto("Placa del vehiculo");
        double capacidad = vista.leerDecimal("Capacidad maxima en kg");
        boolean refrigeracion = vista.leerSiNo("Tiene refrigeracion");

        Vehiculo.validarDatosVehiculo(placa, capacidad);
        sistema.validarPlacaDisponible(placa);

        sistema.registrarVehiculo(new Vehiculo(placa, capacidad, refrigeracion));
        vista.mostrarMensaje("Vehiculo registrado correctamente");
    }

    private void crearDespacho() {
        String codigo = vista.leerTexto("Codigo del despacho");
        String placa = vista.leerTexto("Placa del vehiculo");

        sistema.validarCreacionDespacho(codigo, placa);
        sistema.crearDespacho(codigo, placa);
        vista.mostrarMensaje("Despacho creado correctamente");
    }

    private void asignarEnvio() {
        String codigoDespacho = vista.leerTexto("Codigo del despacho");
        String codigoEnvio = vista.leerTexto("Codigo del envio");

        sistema.validarAsignacionEnvio(codigoDespacho, codigoEnvio);
        sistema.asignarEnvio(codigoDespacho, codigoEnvio);
        vista.mostrarMensaje("Envio asignado correctamente");
    }

    private void retirarEnvio() {
        String codigoDespacho = vista.leerTexto("Codigo del despacho");
        String codigoEnvio = vista.leerTexto("Codigo del envio");

        sistema.validarRetiroEnvio(codigoDespacho, codigoEnvio);
        sistema.retirarEnvio(codigoDespacho, codigoEnvio);
        vista.mostrarMensaje("Envio retirado correctamente");
    }

    private void hacerSalirDespacho() {
        String codigo = vista.leerTexto("Codigo del despacho");

        sistema.validarSalidaDespacho(codigo);
        sistema.hacerSalirDespacho(codigo);
        vista.mostrarMensaje("El despacho ha salido a ruta");
    }

    private void registrarResultado() {
        String codigoDespacho = vista.leerTexto("Codigo del despacho");
        String codigoEnvio = vista.leerTexto("Codigo del envio");

        sistema.validarResultadoEntrega(codigoDespacho, codigoEnvio);
        boolean entregado = vista.leerSiNo("El envio fue entregado");

        sistema.registrarResultadoEntrega(codigoDespacho, codigoEnvio, entregado);
        vista.mostrarMensaje("Resultado registrado correctamente");
    }

    private void consultarEnvios() {
        if (sistema.consultarEnvios().isEmpty()) {
            vista.mostrarMensaje("No hay envios registrados");
            return;
        }

        for (Envio envio : sistema.consultarEnvios()) vista.mostrarMensaje(envio.toString());
    }

    private void consultarVehiculos() {
        if (sistema.consultarVehiculos().isEmpty()) {
            vista.mostrarMensaje("No hay vehiculos registrados");
            return;
        }

        for (Vehiculo vehiculo : sistema.consultarVehiculos()) {
            vista.mostrarMensaje(vehiculo.toString());
            vista.mostrarMensaje("Disponible: " + (sistema.vehiculoEstaLibre(vehiculo) ? "Si" : "No"));
        }
    }

    private void generarReporteEnvios() {
        vista.mostrarReporteEnvios(sistema.contarPorCategoriaYEstado());
    }

    private void generarReporteIngresos() {
        vista.mostrarReporteIngresos(
            sistema.ingresoPorRegistro(),
            sistema.ingresoPorReintentos(),
            sistema.ingresoTotal(),
            sistema.ingresoPorCategoria()
        );
    }

    private void generarReporteDespachos() {
        vista.mostrarReporteDespachos(sistema.despachosNoCerrados());
    }
}
