import java.util.ArrayList;
import java.util.List;

public class Main {
    private ConfiguracionSistema config;
    private BuzonEntrada buzonEntrada;
    private BuzonCuarentena buzonCuarentena;
    private BuzonEntrega buzonEntrega;
    private List<ClienteEmisor> clientes;
    private List<FiltroSpam> filtros;
    private List<ServidorEntrega> servidores;
    private ManejadorCuarentena manejadorCuarentena;
    
    public Main(ConfiguracionSistema config) {
        this.config = config;
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        System.out.println("=".repeat(60));
        System.out.println("INICIALIZANDO SISTEMA DE MENSAJERÍA");
        System.out.println("=".repeat(60));
        System.out.println(config);
        System.out.println("=".repeat(60));
        
        // Resetear contadores estáticos de los filtros
        FiltroSpam.resetearContadores();
        
        // Crear buzones
        buzonEntrada = new BuzonEntrada(config.getCapacidadBuzonEntrada());
        buzonCuarentena = new BuzonCuarentena();
        buzonEntrega = new BuzonEntrega(
            config.getCapacidadBuzonEntrega(), 
            config.getNumeroServidores()
        );
        
        // Crear clientes emisores
        clientes = new ArrayList<>();
        for (int i = 1; i <= config.getNumeroClientes(); i++) {
            ClienteEmisor cliente = new ClienteEmisor(
                i, 
                config.getMensajesPorCliente(), 
                buzonEntrada
            );
            clientes.add(cliente);
        }
        
        // Crear filtros de spam
        filtros = new ArrayList<>();
        for (int i = 1; i <= config.getNumeroFiltros(); i++) {
            FiltroSpam filtro = new FiltroSpam(
                i, 
                buzonEntrada, 
                buzonCuarentena, 
                buzonEntrega,
                config.getNumeroClientes()
            );
            filtros.add(filtro);
        }
        
        // Crear manejador de cuarentena
        manejadorCuarentena = new ManejadorCuarentena(
            buzonCuarentena, 
            buzonEntrega
        );
        
        // Crear servidores de entrega
        servidores = new ArrayList<>();
        for (int i = 1; i <= config.getNumeroServidores(); i++) {
            ServidorEntrega servidor = new ServidorEntrega(i, buzonEntrega);
            servidores.add(servidor);
        }
    }
    
    public void ejecutar() {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("INICIANDO SISTEMA");
            System.out.println("=".repeat(60) + "\n");
            

            System.out.println("Iniciando servidores de entrega...");
            for (ServidorEntrega servidor : servidores) {
                servidor.start();
            }
            Thread.sleep(100);

            System.out.println("Iniciando manejador de cuarentena...");
            manejadorCuarentena.start();
            Thread.sleep(100);

            System.out.println("Iniciando filtros de spam...");
            for (FiltroSpam filtro : filtros) {
                filtro.start();
            }
            Thread.sleep(100);
            
            System.out.println("Iniciando clientes emisores...");
            for (ClienteEmisor cliente : clientes) {
                cliente.start();
            }
            
            System.out.println("\n" + "==========================");
            System.out.println("SISTEMA EN EJECUCIÓN");
            System.out.println("=========================="+"\n");
            

            System.out.println("Esperando que los clientes terminen...");
            for (ClienteEmisor cliente : clientes) {
                cliente.join();
            }
            System.out.println("Todos los clientes han terminado\n");
            
            System.out.println("Esperando que los filtros terminen...");
            for (FiltroSpam filtro : filtros) {
                filtro.join();
            }
            System.out.println("Todos los filtros han terminado\n");
            
            System.out.println("Esperando que el manejador de cuarentena termine...");
            manejadorCuarentena.join();
            System.out.println("Manejador de cuarentena terminado\n");
            

            System.out.println("Esperando que los servidores terminen...");
            for (ServidorEntrega servidor : servidores) {
                servidor.join();
            }
            System.out.println("Todos los servidores han terminado\n");
            
            mostrarEstadisticas();
            
        } catch (InterruptedException e) {
            System.err.println("Error: Sistema interrumpido - " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    private void mostrarEstadisticas() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ESTADÍSTICAS FINALES");
        System.out.println("=".repeat(60));
        
        int totalMensajesEsperados = config.getNumeroClientes() * 
                                      config.getMensajesPorCliente();
        
        int totalProcesado = 0;
        for (ServidorEntrega servidor : servidores) {
            int procesados = servidor.getMensajesProcesados();
            System.out.println(servidor.getName() + ": " + procesados + 
                " mensajes procesados");
            totalProcesado += procesados;
        }
        
        System.out.println("\nRESUMEN:");
        System.out.println("  Mensajes esperados: " + totalMensajesEsperados);
        System.out.println("  Mensajes procesados: " + totalProcesado);
        System.out.println("  Buzón entrada: " + 
            (buzonEntrada.estaVacio() ? "VACÍO " : "CON MENSAJES"));
        System.out.println("  Buzón cuarentena: " + 
            (buzonCuarentena.estaVacio() ? "VACÍO" : "CON MENSAJES"));
        System.out.println("  Buzón entrega: " + 
            (buzonEntrega.estaVacio() ? "VACÍO" : "CON MENSAJES"));
        
        System.out.println("=".repeat(60));
        System.out.println("SISTEMA FINALIZADO CORRECTAMENTE");
        System.out.println("=".repeat(60));
    }
    
    public static void main(String[] args) {
        try {
            ConfiguracionSistema config;
            
            if (args.length > 0) {
                System.out.println("Cargando configuración desde: " + args[0]);
                config = new ConfiguracionSistema(args[0]);
            } else {
                System.out.println("Usando configuración por defecto");
                config = new ConfiguracionSistema();
            }
            
            Main sistema = new Main(config);
            sistema.ejecutar();
            
        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
