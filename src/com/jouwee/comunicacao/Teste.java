package com.jouwee.comunicacao;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

/**
 *
 * @author Nícolas Pohren
 */
public class Teste {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        
        
        // captura a lista de portas disponíveis,
// pelo método estético em CommPortIdentifier.
        Enumeration pList = CommPortIdentifier.getPortIdentifiers();
// Um mapping de nomes para CommPortIdentifiers.
        HashMap map = new HashMap();
// Procura pela porta desejada
        while (pList.hasMoreElements()) {
            CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
            map.put(cpi.getName(), cpi);
            if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("Porta disponivel: " + cpi.getName());
            }
        }

        int timeout = 1000; // Tempo de espera.
        String tipoPorta = args[0]; //Porta serial no linux Fedora
        CommPortIdentifier cp = CommPortIdentifier.getPortIdentifier(tipoPorta);
        SerialPort porta = (SerialPort) cp.open("titulo", timeout);

        InputStream entrada = porta.getInputStream();
        OutputStream saida = porta.getOutputStream();
        porta.setSerialPortParams(115200, porta.DATABITS_8, porta.STOPBITS_2, porta.PARITY_NONE);

        System.out.println("Eu sou a " + tipoPorta);
        
        String msg = "Eu estou sendo enviado de " + tipoPorta;
        saida.write(msg.getBytes());
        Thread.sleep(100);
        saida.flush();

        porta.addEventListener(new SerialPortEventListener() {
            public void serialEvent(SerialPortEvent ev) {
                switch (ev.getEventType()) {
                    //… 
                    case SerialPortEvent.DATA_AVAILABLE:
                        byte[] bufferLeitura = new byte[256];
                        int nodeBytes = -1;
                        try {
                            while (entrada.available() > 0) {
                                nodeBytes = entrada.read(bufferLeitura);
                            }
                            String Dadoslidos = new String(bufferLeitura);
                            if (bufferLeitura.length == 0) {
                                System.out.println("Nada lido!");
                            } else if (bufferLeitura.length == 1) {
                                System.out.println("Apenas um byte foi lido!");
                            } else {
                                System.out.println(Dadoslidos);
                            }
                        } catch (Exception e) {
                            System.out.println("Erro durante a leitura: " + e);
                        }
                        System.out.println("no de bytes lidos : " + nodeBytes);
                        break;
                }
            }

        });
        porta.notifyOnDataAvailable(true);

    }

}
