import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfiguracionSistema {
    private int numeroClientes;
    private int mensajesPorCliente;
    private int numeroFiltros;
    private int numeroServidores;
    private int capacidadBuzonEntrada;
    private int capacidadBuzonEntrega;
    
    public ConfiguracionSistema(String rutaArchivo) throws IOException {
        cargarConfiguracion(rutaArchivo);
    }
    public ConfiguracionSistema() {
        this.numeroClientes = 3;
        this.mensajesPorCliente = 10;
        this.numeroFiltros = 2;
        this.numeroServidores = 2;
        this.capacidadBuzonEntrada = 5;
        this.capacidadBuzonEntrega = 10;
    }
    
    private void cargarConfiguracion(String rutaArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }

                String[] partes = linea.split("=");
                if (partes.length == 2) {
                    String parametro = partes[0].trim().toLowerCase();
                    int valor = Integer.parseInt(partes[1].trim());
                    
                    switch (parametro) {
                        case "numero_clientes":
                        case "numeroclientes":
                            numeroClientes = valor;
                            break;
                        case "mensajes_por_cliente":
                        case "mensajesporcliente":
                            mensajesPorCliente = valor;
                            break;
                        case "numero_filtros":
                        case "numerofiltros":
                            numeroFiltros = valor;
                            break;
                        case "numero_servidores":
                        case "numeroservidores":
                            numeroServidores = valor;
                            break;
                        case "capacidad_buzon_entrada":
                        case "capacidadbuzonentrada":
                            capacidadBuzonEntrada = valor;
                            break;
                        case "capacidad_buzon_entrega":
                        case "capacidadbuzonentrega":
                            capacidadBuzonEntrega = valor;
                            break;
                    }
                }
            }
        }
        
        validarConfiguracion();
    }
    
    private void validarConfiguracion() {
        if (numeroClientes <= 0 || mensajesPorCliente <= 0 || 
            numeroFiltros <= 0 || numeroServidores <= 0 ||
            capacidadBuzonEntrada <= 0 || capacidadBuzonEntrega <= 0) {
            throw new IllegalArgumentException(
                "Todos los parámetros deben ser mayores que cero");
        }
    }
    
    public int getNumeroClientes() {
        return numeroClientes;
    }
    
    public int getMensajesPorCliente() {
        return mensajesPorCliente;
    }
    
    public int getNumeroFiltros() {
        return numeroFiltros;
    }
    
    public int getNumeroServidores() {
        return numeroServidores;
    }
    
    public int getCapacidadBuzonEntrada() {
        return capacidadBuzonEntrada;
    }
    
    public int getCapacidadBuzonEntrega() {
        return capacidadBuzonEntrega;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Configuración:\n" +
            "  Clientes: %d\n" +
            "  Mensajes por cliente: %d\n" +
            "  Filtros de spam: %d\n" +
            "  Servidores de entrega: %d\n" +
            "  Capacidad buzón entrada: %d\n" +
            "  Capacidad buzón entrega: %d",
            numeroClientes, mensajesPorCliente, numeroFiltros, 
            numeroServidores, capacidadBuzonEntrada, capacidadBuzonEntrega
        );
    }
}
