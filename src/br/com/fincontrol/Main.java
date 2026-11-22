package br.com.fincontrol;

import br.com.fincontrol.ui.App;
import javax.swing.SwingUtilities;

/**
 * Abre o FinControl. Os dados ficam no MySQL (banco FinControlDB):
 * antes de executar, rode o script sql/fincontrol.sql no MySQL Workbench.
 *
 * Usuários de teste:
 *   Administrador: breno@gmail.com / 123456
 *   Usuário comum: ana@gmail.com / ana123
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
