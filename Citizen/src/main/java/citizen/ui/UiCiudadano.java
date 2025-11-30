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

import citizen.controller.CitizenController;


public class UiCiudadano extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CitizenController controller;

    private final JTextField origenField;
    private final JTextField destinoField;

    
    public UiCiudadano() {
        super("Sistema Ciudadano – Consulta de Tiempo de Viaje");
        this.controller = new CitizenController(this);

        
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.add(new JLabel("ID parada origen:"));
        origenField = new JTextField(6);
        queryPanel.add(origenField);
        queryPanel.add(new JLabel("ID parada destino:"));
        destinoField = new JTextField(6);
        queryPanel.add(destinoField);
        JButton consultarButton = new JButton("Consultar");
        queryPanel.add(consultarButton);
        // Botón para cancelar consultas
        JButton cancelarButton = new JButton("Cancelar");
        queryPanel.add(cancelarButton);

        // Configuración de la ventana
        setLayout(new BorderLayout());
        
        add(queryPanel, BorderLayout.NORTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Listeners de botones
        consultarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarConsulta();
            }
        });
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCancelacion();
            }
        });
    }

    
    private void realizarConsulta() {
        try {
            long origen = Long.parseLong(origenField.getText());
            long destino = Long.parseLong(destinoField.getText());
            controller.consultar(origen, destino);
        } catch (NumberFormatException ex) {
            mostrarInformacionAlCiudadano("Por favor ingrese identificadores de parada numéricos.");
        }
    }

    
    private void realizarCancelacion() {
        try {
            long origen = Long.parseLong(origenField.getText());
            long destino = Long.parseLong(destinoField.getText());
            String key = origen + "-" + destino;
            controller.detenerConsulta(key);
        } catch (NumberFormatException ex) {
            mostrarInformacionAlCiudadano("Por favor ingrese identificadores de parada numéricos para cancelar.");
        }
    }

    
    public void mostrarInformacionAlCiudadano(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UiCiudadano();
            }
        });
    }
}