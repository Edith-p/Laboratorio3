
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
        vista.mostrarMensaje("""
            1. Sobre
            2. Paquete
            3. Paquete fragil
            4. Carga refrigerada
            """);

        int categoria;
        while (true) {
            categoria = vista.leerEntero("Seleccione la categoria");
            if (categoria >= 1 && categoria <= 4) break;
            vista.mostrarError("Categoria no valida");
        }

        double pesoReal;
        while (true) {
            pesoReal = vista.leerDecimal("Peso real en kg");
            try {
                if (pesoReal <= 0) throw new IllegalArgumentException("El peso debe ser mayor que cero");
                if (categoria == 1) Sobre.validarPeso(pesoReal);
                break;
            } catch (IllegalArgumentException e) {
                vista.mostrarError(e.getMessage());
            }
        }

        TipoDestino destino;
        while (true) {
            destino = vista.leerDestino();
            if (categoria != 4 || destino == TipoDestino.URBANO) break;
            vista.mostrarError("La carga refrigerada solo permite destino urbano");
        }

        double largo = 0, ancho = 0, alto = 0, valor = 0, temperatura = 0;

        if (categoria == 2 || categoria == 3) {
            while (true) {
                largo = vista.leerDecimal("Largo en cm");
                if (largo > 0) break;
                vista.mostrarError("El largo debe ser mayor que cero");
            }

            while (true) {
                ancho = vista.leerDecimal("Ancho en cm");
                if (ancho > 0) break;
                vista.mostrarError("El ancho debe ser mayor que cero");
            }

            while (true) {
                alto = vista.leerDecimal("Alto en cm");
                try {
                    Paquete.validarDimensiones(largo, ancho, alto);
                    break;
                } catch (IllegalArgumentException e) {
                    vista.mostrarError(e.getMessage());
                }
            }
        }

        if (categoria == 3) {
            while (true) {
                valor = vista.leerDecimal("Valor declarado en Q");
                try {
                    PaqueteFragil.validarValorDeclarado(valor);
                    break;
                } catch (IllegalArgumentException e) {
                    vista.mostrarError(e.getMessage());
                }
            }
        }

        if (categoria == 4) {
            while (true) {
                temperatura = vista.leerDecimal("Temperatura requerida");
                try {
                    CargaRefrigerada.validarRefrigeracion(destino, temperatura);
                    break;
                } catch (IllegalArgumentException e) {
                    vista.mostrarError(e.getMessage());
                }
            }
        }

        Envio cotizacion;

        // Usamos "TEMP" en varios campos solo para poder construir un objeto temporal de la cotizacion. Estos nunca se registran ni afectan otros datos del sistema.
        switch (categoria) {
            case 1 -> cotizacion = new Sobre("TEMP", "TEMP", "TEMP", pesoReal, destino);
            case 2 -> cotizacion = new Paquete("TEMP", "TEMP", "TEMP", pesoReal, destino, largo, ancho, alto);
            case 3 -> cotizacion = new PaqueteFragil("TEMP", "TEMP", "TEMP", pesoReal, destino, largo, ancho, alto, valor);
            case 4 -> cotizacion = new CargaRefrigerada("TEMP", "TEMP", "TEMP", pesoReal, destino, temperatura);
            default -> throw new IllegalArgumentException("Categoria no valida");
        }

        vista.mostrarCotizacion(cotizacion);

        if (!vista.leerSiNo("Desea registrar el envio")) {
            vista.mostrarMensaje("Cotizacion finalizada sin registrar el envio");
            return;
        }

        String codigo;
        while (true) {
            codigo = vista.leerTexto("Codigo del envio");
            try {
                sistema.validarCodigoEnvioDisponible(codigo);
                break;
            } catch (IllegalArgumentException e) {
                vista.mostrarError(e.getMessage());
            }
        }

        String remitente = vista.leerTexto("Nombre del remitente");
        String destinatario = vista.leerTexto("Nombre del destinatario");

        Envio envio;

        switch (categoria) {
            case 1 -> envio = new Sobre(codigo, remitente, destinatario, pesoReal, destino);
            case 2 -> envio = new Paquete(codigo, remitente, destinatario, pesoReal, destino, largo, ancho, alto);
            case 3 -> envio = new PaqueteFragil(codigo, remitente, destinatario, pesoReal, destino, largo, ancho, alto, valor);
            case 4 -> envio = new CargaRefrigerada(codigo, remitente, destinatario, pesoReal, destino, temperatura);
            default -> throw new IllegalArgumentException("Categoria no valida");
        }

        sistema.registrarEnvio(envio);
        vista.mostrarMensaje("Envio registrado correctamente");
    }





    private void registrarVehiculo() {
        String placa;
        while (true) {
            placa = vista.leerTexto("Placa del vehiculo");
            try {
                sistema.validarPlacaDisponible(placa);
                break;
            } catch (IllegalArgumentException e) {
                vista.mostrarError(e.getMessage());
            }
        }

        double capacidad;
        while (true) {
            capacidad = vista.leerDecimal("Capacidad maxima en kg");
            try {
                Vehiculo.validarDatosVehiculo(placa, capacidad);
                break;
            } catch (IllegalArgumentException e) {
                vista.mostrarError(e.getMessage());
            }
        }

        boolean refrigeracion = vista.leerSiNo("Tiene refrigeracion");
        sistema.registrarVehiculo(new Vehiculo(placa, capacidad, refrigeracion));
        vista.mostrarMensaje("Vehiculo registrado correctamente :P");
    }


    
    private void crearDespacho() {
        String codigo;
        while (true) {
            codigo = vista.leerTexto("Codigo del despacho");
            try {
                sistema.validarCodigoDespachoDisponible(codigo);
                break;
            } catch (IllegalArgumentException e) {
                vista.mostrarError(e.getMessage());
            }
        }

        String placa;
        while (true) {
            placa = vista.leerTexto("Placa del vehiculo");
            try {
                sistema.validarCreacionDespacho(codigo, placa);
                break;
            } catch (IllegalArgumentException | IllegalStateException e) {
                vista.mostrarError(e.getMessage());
            }
        }

        sistema.crearDespacho(codigo, placa);
        vista.mostrarMensaje("Despacho creado correctamente");
    }


    
    private void asignarEnvio() {
        String codigoDespacho = vista.leerTexto("Codigo del despacho");
        String codigoEnvio;

        while (true) {
            codigoEnvio = vista.leerTexto("Codigo del envio");
            try {
                sistema.validarAsignacionEnvio(codigoDespacho, codigoEnvio);
                break;
            } catch (IllegalArgumentException | IllegalStateException e) {
                vista.mostrarError(e.getMessage());
                return;
            }
        }

        sistema.asignarEnvio(codigoDespacho, codigoEnvio);
        vista.mostrarMensaje("Envio asignado correctamente");
    }


    
    private void retirarEnvio() {
        String codigoDespacho = vista.leerTexto("Codigo del despacho");
        String codigoEnvio = vista.leerTexto("Codigo del envio");

        sistema.validarRetiroEnvio(codigoDespacho, codigoEnvio);
        sistema.retirarEnvio(codigoDespacho, codigoEnvio);
        vista.mostrarMensaje("Envio retirado correctamente :D");
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
        vista.mostrarMensaje("\n========== ENVIOS REGISTRADOS ==========\n");
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
