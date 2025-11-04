public class ServidorEntrega extends Thread {
    private final int id;
    private final BuzonEntrega buzonEntrega;
    private int mensajesProcesados;
    
    public ServidorEntrega(int id, BuzonEntrega buzonEntrega) {
        super("Servidor-" + id);
        this.id = id;
        this.buzonEntrega = buzonEntrega;
        this.mensajesProcesados = 0;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("[" + getName() + "] Iniciando");
            
            while (true) {
                Mensaje mensaje = buzonEntrega.retirar();
                
                if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
                    System.out.println("[" + getName() + 
                        "] Mensaje FIN recibido. Total procesados: " + 
                        mensajesProcesados);
                    break;
                }
                
                mensajesProcesados++;
                System.out.println("[" + getName() + "] Procesando: " + mensaje + 
                    " (Total: " + mensajesProcesados + ")");
                
                int tiempoProcesamiento = 50 + (int)(Math.random() * 150);
                Thread.sleep(tiempoProcesamiento);
                
                System.out.println("[" + getName() + "] Mensaje procesado: " + 
                    mensaje);
            }
            
            System.out.println("[" + getName() + "] Terminado. Total procesados: " + 
                mensajesProcesados);
            
        } catch (InterruptedException e) {
            System.err.println("[" + getName() + "] Interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    public int getMensajesProcesados() {
        return mensajesProcesados;
    }
}
