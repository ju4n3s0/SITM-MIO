package citizen.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import citizen.controller.CitizenController;
import citizen.service.ReceptorDeInformacionSistemaCiudadano;


public class UiCiudadano extends JFrame {

    private JTextField campoOrigen;
    private JTextField campoDestino;
    private JTextArea areaResultado;
    private CitizenController controller;

    public UiCiudadano() {
        super("Consulta de Tiempo Promedio de Viaje");
        inicializarComponentes();
    }

    
    private void inicializarComponentes() {
        // Panel superior con campos de entrada
        JPanel panelEntradas = new JPanel(new FlowLayout());
        panelEntradas.add(new JLabel("Id parada origen:"));
        campoOrigen = new JTextField(10);
        panelEntradas.add(campoOrigen);

        panelEntradas.add(new JLabel("Id parada destino:"));
        campoDestino = new JTextField(10);
        panelEntradas.add(campoDestino);

        JButton botonConsultar = new JButton("Consultar");
        panelEntradas.add(botonConsultar);

        // Área de resultados
        areaResultado = new JTextArea(5, 40);
        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);

        // Agregar componentes al frame
        this.setLayout(new BorderLayout());
        this.add(panelEntradas, BorderLayout.NORTH);
        this.add(areaResultado, BorderLayout.CENTER);

        // Crear receptor y controlador
        ReceptorDeInformacionSistemaCiudadano receptor = new ReceptorDeInformacionSistemaCiudadano();
        controller = new CitizenController(this, receptor);

        // Acción del botón
        botonConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarConsulta();
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }

   
    private void manejarConsulta() {
        String textoOrigen = campoOrigen.getText().trim();
        String textoDestino = campoDestino.getText().trim();
        long origenId;
        long destinoId;
        try {
            origenId = Long.parseLong(textoOrigen);
            destinoId = Long.parseLong(textoDestino);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese identificadores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        controller.consultarTiempoPromedio(origenId, destinoId);
    }

    public void mostrarInformacionAlCiudadano(String mensaje) {
        areaResultado.setText(mensaje);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiCiudadano ui = new UiCiudadano();
                ui.setVisible(true);
            }
        });
    }
}