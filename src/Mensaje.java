public class Mensaje {
    public enum Tipo {
        INICIO,
        CORREO,
        FIN
    }
    
    private String id;
    private Tipo tipo;
    private boolean esSpam;
    private int tiempoCuarentena;
    private String contenido;
    
    public Mensaje(Tipo tipo) {
        this.tipo = tipo;
        this.id = tipo.toString();
        this.esSpam = false;
        this.tiempoCuarentena = 0;
        this.contenido = "";
    }
    
    public Mensaje(String id, boolean esSpam, String contenido) {
        this.id = id;
        this.tipo = Tipo.CORREO;
        this.esSpam = esSpam;
        this.contenido = contenido;
        this.tiempoCuarentena = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public boolean esSpam() {
        return esSpam;
    }
    
    public int getTiempoCuarentena() {
        return tiempoCuarentena;
    }
    
    public void setTiempoCuarentena(int tiempo) {
        this.tiempoCuarentena = tiempo;
    }
    
    public void decrementarTiempo() {
        if (tiempoCuarentena > 0) {
            tiempoCuarentena--;
        }
    }
    
    public String getContenido() {
        return contenido;
    }
    
    @Override
    public String toString() {
        if (tipo == Tipo.CORREO) {
            return String.format("Mensaje[id=%s, spam=%b, cuarentena=%d]", 
                id, esSpam, tiempoCuarentena);
        }
        return String.format("Mensaje[tipo=%s]", tipo);
    }
}
