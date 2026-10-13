
import java.util.ArrayList;
import java.util.Scanner;

public class Vista {
    private Scanner scanner;

    public Vista() {
        scanner = new Scanner(System.in);
    }

    public int mostrarMenu() {
        System.out.println("""
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                    ENVIAGT - MENÚ
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            1. Cotizar y registrar envío
            2. Registrar vehículo
            3. Crear despacho
            4. Asignar envío
            5. Retirar envío
            6. Hacer salir despacho
            7. Registrar resultado de entrega
            8. Consultar envíos
            9. Consultar vehículos
            10. Reporte de envíos
            11. Reporte de ingresos
            12. Reporte de despachos
            0. Salir
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            """);

        return leerEntero("Seleccione una opción");
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        return scanner.nextLine().trim();
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje + ": ");
                double numero = Double.parseDouble(scanner.nextLine().trim());
                if (!Double.isFinite(numero)) {
                    System.out.println("Error: ingrese un número válido");
                    continue;
                }
                return numero;
            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar un número decimal");
            }
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje + ": ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar un número entero");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (S/N): ");
            String respuesta = scanner.nextLine().trim();

            if (respuesta.equalsIgnoreCase("S")) return true;
            if (respuesta.equalsIgnoreCase("N")) return false;

            System.out.println("Error: ingrese S o N");
        }
    }

    public TipoDestino leerDestino() {
        while (true) {
            System.out.println("""
                Tipo de destino:
                1. Urbano
                2. Departamental
                """);

            int opcion = leerEntero("Seleccione el destino");

            switch (opcion) {
                case 1 -> { return TipoDestino.URBANO; }
                case 2 -> { return TipoDestino.DEPARTAMENTAL; }
                default -> System.out.println("Error: destino no válido");
            }
        }
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("Error: " + mensaje);
    }

    public void mostrarCotizacion(Envio envio) {
        System.out.println(String.format("""
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                  COTIZACIÓN DE ENVÍO
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            Categoría: %s
            Costo: %s
            Días estimados: %d
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            """,
            envio.getCategoria(),
            formatearMonto(envio.calcularCosto()),
            envio.calcularDiasEntrega()
        ));
    }

    public void mostrarReporteEnvios(String datos) {
        System.out.println(datos);
    }

    public void mostrarReporteIngresos(double registro, double reintentos, double total, String porCategoria) {
        System.out.println(String.format("""
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   REPORTE DE INGRESOS
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            Ingresos por registro: %s
            Ingresos por reintentos: %s
            Ingreso total: %s

            %s
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            """,
            formatearMonto(registro),
            formatearMonto(reintentos),
            formatearMonto(total),
            porCategoria
        ));
    }

    public void mostrarReporteDespachos(ArrayList<Despacho> despachos) {
        System.out.println("""
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                  DESPACHOS NO CERRADOS
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            """);

        if (despachos.isEmpty()) {
            System.out.println("No hay despachos activos");
            return;
        }

        for (Despacho despacho : despachos) {
            System.out.println(despacho);
        }
    }

    public static String formatearMonto(double monto) {
        return String.format("Q%.2f", monto);
    }
}