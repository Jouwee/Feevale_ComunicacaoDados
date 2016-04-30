package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

/**
 *
 * 
 * @author Nícolas Pohren
 */
public class Aplicacao {
    
    public static void main(String[] args) {
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
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
