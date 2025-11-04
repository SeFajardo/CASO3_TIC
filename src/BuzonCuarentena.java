import java.util.ArrayList;
import java.util.List;

public class BuzonCuarentena {
    private final List<Mensaje> mensajes;
    private boolean finRecibido;
    
    public BuzonCuarentena() {
        this.mensajes = new ArrayList<>();
        this.finRecibido = false;
    }
    
    public synchronized void depositar(Mensaje mensaje) {
        if (mensaje.getTipo() == Mensaje.Tipo.CORREO) {
            int tiempoAleatorio = 10000 + (int)(Math.random() * 10001);
            mensaje.setTiempoCuarentena(tiempoAleatorio);
            mensajes.add(mensaje);
            System.out.println("[BuzonCuarentena] Depositado spam: " + mensaje + 
                " | Tamaño: " + mensajes.size());
        } else if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
            finRecibido = true;
            System.out.println("[BuzonCuarentena] Mensaje FIN recibido");
        }
        
        notify();
    }

    public synchronized List<Mensaje> obtenerTodos() {
        return new ArrayList<>(mensajes);
    }
    
    public synchronized void remover(Mensaje mensaje) {
        mensajes.remove(mensaje);
        System.out.println("[BuzonCuarentena] Removido: " + mensaje + 
            " | Tamaño: " + mensajes.size());
    }
    
    public synchronized boolean estaVacio() {
        return mensajes.isEmpty();
    }
    
    public synchronized boolean finRecibido() {
        return finRecibido;
    }
    
    public synchronized int getTamaño() {
        return mensajes.size();
    }
}
