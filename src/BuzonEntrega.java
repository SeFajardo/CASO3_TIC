import java.util.LinkedList;
import java.util.Queue;

public class BuzonEntrega {
    private final Queue<Mensaje> mensajes;
    private final int capacidadMaxima;
    private boolean finDepositado;
    private int servidoresNotificados;
    private final int totalServidores;
    
    public BuzonEntrega(int capacidadMaxima, int totalServidores) {
        this.mensajes = new LinkedList<>();
        this.capacidadMaxima = capacidadMaxima;
        this.finDepositado = false;
        this.servidoresNotificados = 0;
        this.totalServidores = totalServidores;
    }
    
    public synchronized void depositar(Mensaje mensaje) {
        while (mensajes.size() >= capacidadMaxima) {
            try {
                wait(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
            finDepositado = true;
            for (int i = 0; i < totalServidores; i++) {
                mensajes.add(new Mensaje(Mensaje.Tipo.FIN));
            }
            System.out.println("[BuzonEntrega] Mensaje FIN depositado (copias: " + 
                totalServidores + ")");
        } else {
            mensajes.add(mensaje);
            System.out.println("[BuzonEntrega] Depositado: " + mensaje + 
                " | Tamaño: " + mensajes.size() + "/" + capacidadMaxima);
        }
        
        notifyAll();
    }
    
    public synchronized Mensaje retirar() {
        while (mensajes.isEmpty()) {
            try {
                wait(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        Mensaje mensaje = mensajes.poll();
        
        if (mensaje != null && mensaje.getTipo() == Mensaje.Tipo.FIN) {
            servidoresNotificados++;
        }
        
        System.out.println("[BuzonEntrega] Retirado: " + mensaje + 
            " | Tamaño: " + mensajes.size() + "/" + capacidadMaxima);
        
        notifyAll();
        
        return mensaje;
    }
    
    public synchronized boolean estaVacio() {
        return mensajes.isEmpty();
    }
    
    public synchronized boolean finDepositado() {
        return finDepositado;
    }
    
    public synchronized boolean todosServidoresNotificados() {
        return servidoresNotificados >= totalServidores;
    }
    
    public synchronized int getTamaño() {
        return mensajes.size();
    }
}