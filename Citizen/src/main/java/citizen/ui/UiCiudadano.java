package citizen.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import citizen.controller.CitizenController;


public class UiCiudadano extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CitizenController controller;

    private final JTextField usuarioField;
    private final JPasswordField contrasenaField;
    private final JTextField origenField;
    private final JTextField destinoField;

    
    public UiCiudadano() {
        super("Sistema Ciudadano – Consulta de Tiempo de Viaje");
        this.controller = new CitizenController(this);

        // Panel para autenticación
        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        authPanel.add(new JLabel("Usuario:"));
        usuarioField = new JTextField(10);
        authPanel.add(usuarioField);
        authPanel.add(new JLabel("Contraseña:"));
        contrasenaField = new JPasswordField(10);
        authPanel.add(contrasenaField);
        JButton loginButton = new JButton("Iniciar sesión");
        authPanel.add(loginButton);

        // Panel para consulta
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.add(new JLabel("ID parada origen:"));
        origenField = new JTextField(6);
        queryPanel.add(origenField);
        queryPanel.add(new JLabel("ID parada destino:"));
        destinoField = new JTextField(6);
        queryPanel.add(destinoField);
        JButton consultarButton = new JButton("Consultar");
        queryPanel.add(consultarButton);

        // Configuración de la ventana
        setLayout(new BorderLayout());
        add(authPanel, BorderLayout.NORTH);
        add(queryPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Listeners de botones
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarAutenticacion();
            }
        });
        consultarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarConsulta();
            }
        });
    }

    
    private void realizarAutenticacion() {
        String usuario = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());
        boolean ok = controller.autenticarUsuario(usuario, contrasena);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Autenticación exitosa. Puede realizar consultas.");
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas. Inténtelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /
    private void realizarConsulta() {
        try {
            long origen = Long.parseLong(origenField.getText());
            long destino = Long.parseLong(destinoField.getText());
            controller.consultar(origen, destino);
        } catch (NumberFormatException ex) {
            mostrarInformacionAlCiudadano("Por favor ingrese identificadores de parada numéricos.");
        }
    }

    
    public void mostrarInformacionAlCiudadano(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

   
    public static void main(String[] args) {
        // Para garantizar la apariencia consistente de la interfaz
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UiCiudadano();
            }
        });
    }
}