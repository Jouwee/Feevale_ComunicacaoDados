package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.BorderLayout;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.JFrame;

/**
 *
 *
 * @author Nícolas Pohren
 */
public class Aplicacao extends JFrame {

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
        SerialPort porta = (SerialPort) cp.open("titulo", 1000);

        SerialComm comm = new SerialComm(porta);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        Game game;
        if (args[0].equals("COM1")) {
            game = new GameServer(comm);
        } else {
            game = new GameClient(comm);
        }
        getContentPane().add(new GamePanel(game));
        game.startGameLoop();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void metodo(String[] args) {
        try {
            CommPortIdentifier cp = CommPortIdentifier.getPortIdentifier(args[0]);
            SerialPort porta = (SerialPort) cp.open("titulo", 1000);

            SerialComm comm = new SerialComm(porta);

            if (args[0].equals("COM2")) {

                while (true) {
                    System.out.println("Enter para enviar...");
                    System.in.read();

                    System.out.println("Enviando...");
                    comm.send("What is this".getBytes());

                }
            } else {
                while (true) {
                    System.out.println("reading");
                    byte[] bytes = comm.receive();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
