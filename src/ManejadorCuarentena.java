import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class ManejadorCuarentena extends Thread {
    private final BuzonCuarentena buzonCuarentena;
    private final BuzonEntrega buzonEntrega;
    private final Random random;
    
    public ManejadorCuarentena(BuzonCuarentena buzonCuarentena, 
                               BuzonEntrega buzonEntrega) {
        super("ManejadorCuarentena");
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
        this.random = new Random();
    }
    
    @Override
    public void run() {
        try {
            System.out.println("[" + getName() + "] Iniciando...");
            
            while (true) {
                if (buzonCuarentena.estaVacio() && !buzonCuarentena.finRecibido()) {
                    Thread.yield();
                    Thread.sleep(100);
                    continue;
                }
                
                if (buzonCuarentena.finRecibido() && buzonCuarentena.estaVacio()) {
                    System.out.println("[" + getName() + 
                        "] FIN recibido y buzón vacío. Terminando...");
                    break;
                }
                
                List<Mensaje> mensajes = buzonCuarentena.obtenerTodos();
                List<Mensaje> mensajesAProcesar = new ArrayList<>(mensajes);
                
                for (Mensaje mensaje : mensajesAProcesar) {
                    mensaje.decrementarTiempo();
                    
                    int numeroAleatorio = random.nextInt(21) + 1;
                    
                    if (numeroAleatorio % 7 == 0) {
                        buzonCuarentena.remover(mensaje);
                        System.out.println("[" + getName() + 
                            "] Mensaje descartado (malicioso): " + mensaje);
                        continue;
                    }

                    if (mensaje.getTiempoCuarentena() <= 0) {
                        buzonCuarentena.remover(mensaje);
                        buzonEntrega.depositar(mensaje);
                        System.out.println("[" + getName() + 
                            "] Mensaje pasó cuarentena y se envió a entrega: " + 
                            mensaje);
                    } else {
                        System.out.println("[" + getName() + 
                            "] Procesando: " + mensaje + 
                            " (tiempo restante: " + mensaje.getTiempoCuarentena() + ")");
                    }
                }

                Thread.sleep(1000);
            }
            
            System.out.println("[" + getName() + "] Terminado");
            
        } catch (InterruptedException e) {
            System.err.println("[" + getName() + "] Interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
