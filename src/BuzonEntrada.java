import java.util.LinkedList;
import java.util.Queue;

public class BuzonEntrada {
    private final Queue<Mensaje> mensajes;
    private final int capacidadMaxima;
    private int contadorMensajesInicio;
    private int contadorMensajesFin;
    
    public BuzonEntrada(int capacidadMaxima) {
        this.mensajes = new LinkedList<>();
        this.capacidadMaxima = capacidadMaxima;
        this.contadorMensajesInicio = 0;
        this.contadorMensajesFin = 0;
    }
    
    public synchronized void depositar(Mensaje mensaje) throws InterruptedException {
        while (mensajes.size() >= capacidadMaxima) {
            wait();
        }
        
        mensajes.add(mensaje);
        
        if (mensaje.getTipo() == Mensaje.Tipo.INICIO) {
            contadorMensajesInicio++;
        } else if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
            contadorMensajesFin++;
        }
        
        System.out.println("[BuzonEntrada] Depositado: " + mensaje + 
            " | Tamaño: " + mensajes.size() + "/" + capacidadMaxima);
        
        notify();
    }
    
    public synchronized Mensaje retirar() throws InterruptedException {
        while (mensajes.isEmpty()) {
            wait();
        }
        
        Mensaje mensaje = mensajes.poll();
        
        System.out.println("[BuzonEntrada] Retirado: " + mensaje + 
            " | Tamaño: " + mensajes.size() + "/" + capacidadMaxima);
    
        notify();
        
        return mensaje;
    }
    
    public synchronized boolean estaVacio() {
        return mensajes.isEmpty();
    }
    
    public synchronized int getContadorMensajesInicio() {
        return contadorMensajesInicio;
    }
    
    public synchronized int getContadorMensajesFin() {
        return contadorMensajesFin;
    }
    
    public synchronized int getTamaño() {
        return mensajes.size();
    }
}
