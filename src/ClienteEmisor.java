public class ClienteEmisor extends Thread {
    private final int id;
    private final int numeroMensajes;
    private final BuzonEntrada buzonEntrada;
    
    public ClienteEmisor(int id, int numeroMensajes, BuzonEntrada buzonEntrada) {
        super("Cliente-" + id);
        this.id = id;
        this.numeroMensajes = numeroMensajes;
        this.buzonEntrada = buzonEntrada;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("[" + getName() + "] Iniciando");
            
            Mensaje mensajeInicio = new Mensaje(Mensaje.Tipo.INICIO);
            buzonEntrada.depositar(mensajeInicio);
            System.out.println("[" + getName() + "] Mensaje INICIO enviado");
            
            for (int i = 0; i < numeroMensajes; i++) {

                String idMensaje = "Cliente-" + id + "-Mensaje-" + i;
                
                boolean esSpam = Math.random() < 0.3;
                
                String contenido = "Correo del cliente " + id + ", mensaje #" + i;
                Mensaje mensaje = new Mensaje(idMensaje, esSpam, contenido);
                
                buzonEntrada.depositar(mensaje);
                System.out.println("[" + getName() + "] Enviado: " + mensaje);
                
                Thread.sleep(50 + (int)(Math.random() * 100));
            }
            
            Mensaje mensajeFin = new Mensaje(Mensaje.Tipo.FIN);
            buzonEntrada.depositar(mensajeFin);
            System.out.println("[" + getName() + "] Mensaje FIN enviado");
            
            System.out.println("[" + getName() + "] Terminado. Total enviados: " + 
                (numeroMensajes + 2));
            
        } catch (InterruptedException e) {
            System.err.println("[" + getName() + "] Interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
