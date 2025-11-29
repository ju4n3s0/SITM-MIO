package citizen.auth;


public class Autenticador {

    
    public boolean autenticar(String usuario, String contrasena) {
        if (usuario == null || usuario.isEmpty()) return false;
        return "1234".equals(contrasena);
    }
}