public class FiltroSpam extends Thread {
    private final int id;
    private final BuzonEntrada buzonEntrada;
    private final BuzonCuarentena buzonCuarentena;
    private final BuzonEntrega buzonEntrega;
    private final int totalClientes;
    
    private static int clientesInicializados = 0;
    private static int clientesFinalizados = 0;
    private static boolean finEnviado = false;
    
    public static synchronized void resetearContadores() {
        clientesInicializados = 0;
        clientesFinalizados = 0;
        finEnviado = false;
    }
    
    public FiltroSpam(int id, BuzonEntrada buzonEntrada, 
                      BuzonCuarentena buzonCuarentena,
                      BuzonEntrega buzonEntrega,
                      int totalClientes) {
        super("Filtro-" + id);
        this.id = id;
        this.buzonEntrada = buzonEntrada;
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
        this.totalClientes = totalClientes;
    }
    
    @Override
public void run() {
    try {
        System.out.println("[" + getName() + "] Iniciando...");
        
        while (true) {
            // Revisar si todos los clientes terminaron ANTES de intentar retirar
            synchronized (FiltroSpam.class) {
                if (clientesFinalizados >= totalClientes) {
                    System.out.println("[" + getName() + 
                        "] Todos los clientes han terminado (detección proactiva)");
                    break;
                }
            }
            
            Mensaje mensaje = buzonEntrada.retirar();
            
            if (mensaje.getTipo() == Mensaje.Tipo.INICIO) {
                synchronized (FiltroSpam.class) {
                    clientesInicializados++;
                    System.out.println("[" + getName() + 
                        "] Cliente inicializado. Total: " + 
                        clientesInicializados + "/" + totalClientes);
                }
                
            } else if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
                synchronized (FiltroSpam.class) {
                    clientesFinalizados++;
                    System.out.println("[" + getName() + 
                        "] Cliente finalizado. Total: " + 
                        clientesFinalizados + "/" + totalClientes);
                    
                    if (clientesFinalizados >= totalClientes) {
                        System.out.println("[" + getName() + 
                            "] Todos los clientes han terminado");
                        break;
                    }
                }
                
            } else if (mensaje.getTipo() == Mensaje.Tipo.CORREO) {
                if (mensaje.esSpam()) {
                    buzonCuarentena.depositar(mensaje);
                    System.out.println("[" + getName() + 
                        "] Mensaje spam enviado a cuarentena: " + mensaje);
                } else {
                    buzonEntrega.depositar(mensaje);
                    System.out.println("[" + getName() + 
                        "] Mensaje válido enviado a entrega: " + mensaje);
                }
            }
            
            Thread.sleep(20 + (int)(Math.random() * 50));
        }
        
        esperarProcesamientoCompleto();
        
        synchronized (FiltroSpam.class) {
            if (!finEnviado) {
                finEnviado = true;
                buzonEntrega.depositar(new Mensaje(Mensaje.Tipo.FIN));
                buzonCuarentena.depositar(new Mensaje(Mensaje.Tipo.FIN));
                System.out.println("[" + getName() + 
                    "] Mensaje FIN enviado a buzones de entrega y cuarentena");
            }
        }
        
        System.out.println("[" + getName() + "] Terminado");
        
    } catch (InterruptedException e) {
        System.err.println("[" + getName() + "] Interrumpido: " + e.getMessage());
        Thread.currentThread().interrupt();
    }
}
    
    private void esperarProcesamientoCompleto() throws InterruptedException {
        while (!buzonEntrada.estaVacio() || !buzonCuarentena.estaVacio()) {
            System.out.println("[" + getName() + 
                "] Esperando que buzones se vacíen... Entrada: " + 
                buzonEntrada.getTamaño() + ", Cuarentena: " + 
                buzonCuarentena.getTamaño());
            Thread.sleep(100);
        }
    }
}
