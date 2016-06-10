package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.BorderLayout;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 *
 * @author Nícolas Pohren
 */
public class Aplicacao extends JFrame {

    private Game game;
    private SerialComm comm;

    public static void main(String[] args) {
        Aplicacao app = new Aplicacao(args);
        app.setVisible(true);
    }

    /**
     * Cria a aplicação
     */
    public Aplicacao(String[] args) {
        super();
        try {
            CommPortIdentifier cp = CommPortIdentifier.getPortIdentifier(args[0]);
            SerialPort porta = (SerialPort) cp.open(args[0], 1000);

            comm = new SerialComm(porta);

            if (args[0].equals("COM1")) {
                game = new GameServer(comm);
            } else {
                game = new GameClient(comm);
            }
            game.startGameLoop();

        } catch (Exception e) {
            e.printStackTrace();
        }
        initGui();
    }

    /**
     * Inicializa a interface
     */
    private void initGui() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new GamePanel(game));
        getContentPane().add(buildControlPanel(), BorderLayout.SOUTH);
        pack();
    }
    
    /**
     * Cria o painel de controle do jogo
     */
    private JComponent buildControlPanel() {
        JPanel panel = new JPanel();
        panel.add(new PanelMonitoramentoSerial(game, comm));
        return panel;
    }

}
