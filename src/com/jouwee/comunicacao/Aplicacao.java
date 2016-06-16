package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 *
 * @author Nícolas Pohren
 */
public class Aplicacao extends JFrame {

    private JComboBox<CommPortIdentifier> comboPortas;
    private Game game;
    private SerialComm comm;
    private PanelMonitoramentoSerial pMonitoramento;
    private GamePanel gamePanel;
    private JButton btServer;
    private JButton btClient;

    public static void main(String[] args) {
        Aplicacao app = new Aplicacao();
        app.setVisible(true);
    }

    /**
     * Cria a aplicação
     */
    public Aplicacao() {
        super();
        initGui();
    }

    /**
     * Inicializa a interface
     */
    private void initGui() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        gamePanel = new GamePanel();
        getContentPane().add(gamePanel);
        getContentPane().add(buildControlPanel(), BorderLayout.SOUTH);
        pack();
    }
    
    /**
     * Cria o painel de controle do jogo
     */
    private JComponent buildControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(buildPanelSelecaoPorta(), BorderLayout.WEST);
        pMonitoramento = new PanelMonitoramentoSerial();
        panel.add(pMonitoramento);
        return panel;
    }
    
    /**
     * Cria o painel de seleção de porta
     * 
     * @return JComponent
     */
    private JComponent buildPanelSelecaoPorta() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(new JLabel("Porta", JLabel.RIGHT));
        panel.add(buildComboPortas());
        btServer = new JButton(new ActionStartServer());
        panel.add(btServer);
        btClient = new JButton(new ActionStartClient());
        panel.add(btClient);
        return panel;
    }
    
    /**
     * Cria a combo de seleção de portas
     */
    private JComponent buildComboPortas() {
        Enumeration<CommPortIdentifier> portas = CommPortIdentifier.getPortIdentifiers();
        List<CommPortIdentifier> nPortas = new ArrayList<>();
        while (portas.hasMoreElements()) {
            nPortas.add(portas.nextElement());
        }
        comboPortas = new JComboBox<>(nPortas.toArray(new CommPortIdentifier[] {}));
        comboPortas.setRenderer(new ListCellRenderer<CommPortIdentifier>() {
            DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
            @Override
            public Component getListCellRendererComponent(JList<? extends CommPortIdentifier> list, CommPortIdentifier value, int index, boolean isSelected, boolean cellHasFocus) {
                return defaultRenderer.getListCellRendererComponent(list, value.getName(), index, isSelected, cellHasFocus);
            }
        }
        );
        return comboPortas;
    }
    
    private class ActionStartServer extends AbstractAction {

        public ActionStartServer() {
            super("Iniciar Server");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                SerialPort porta = (SerialPort) ((CommPortIdentifier)comboPortas.getSelectedItem()).open("Server", 1000);
                comm = new SerialComm(porta);
                game = new GameServer(comm);
                game.startGameLoop();
                pMonitoramento.setGame(game);
                pMonitoramento.setComm(comm);
                gamePanel.setGame(game);
                comboPortas.setEnabled(false);
                btServer.setEnabled(false);
                btClient.setEnabled(false);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        
    }

    private class ActionStartClient extends AbstractAction {

        public ActionStartClient() {
            super("Iniciar Client");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                SerialPort porta = (SerialPort) ((CommPortIdentifier)comboPortas.getSelectedItem()).open("Client", 1000);
                comm = new SerialComm(porta);
                game = new GameClient(comm);
                game.startGameLoop();
                pMonitoramento.setGame(game);
                pMonitoramento.setComm(comm);
                gamePanel.setGame(game);
                comboPortas.setEnabled(false);
                btServer.setEnabled(false);
                btClient.setEnabled(false);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        
    }

}
