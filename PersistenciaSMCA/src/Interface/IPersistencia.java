package Interface;

import Modelo.Medida;
import Modelo.Usuario;

public interface IPersistencia {

    public void connectDatabase();
    public void persistirUsuario(Usuario usuario);
    public void persistirMedida(Medida medida);
    public boolean verificarContrasena(Usuario usuario);

}
