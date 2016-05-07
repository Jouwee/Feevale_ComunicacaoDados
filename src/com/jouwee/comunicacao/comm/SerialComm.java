package com.jouwee.comunicacao.comm;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

/**
 * Comunicação serial
 *
 * @author Nícolas Pohren
 */
public class SerialComm implements SerialPortEventListener {

    /** Porta serial da comunicação */
    private final SerialPort port;
    /** Lock de leitura */
    private final Object lock;
    /** Bytes lidos */
    private byte[] readBytes;
    

    /**
     * Cria nova comunicação serial
     *
     * @param port
     */
    public SerialComm(SerialPort port) {
        this.port = port;
        try {
            port.setSerialPortParams(115200, port.DATABITS_8, port.STOPBITS_2, port.PARITY_NONE);
            port.notifyOnDataAvailable(true);
            this.port.addEventListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock = new Object();
    }

    /**
     * Envia dados
     *
     * @param data
     */
    public void send(byte[] data) {
        Package[] packages = Package.fromData(data);
        for (Package thePackage : packages) {
            send(thePackage);
        }
    }
    
    /**
     * Envia um pacote
     * 
     * @param pack 
     */
    public void send(Package pack) {
        try {
            port.getOutputStream().write(pack.getRaw());
            port.getOutputStream().flush();
        } catch (Exception ex) {
            Logger.getLogger(SerialComm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Aguarda e recebe dados
     *
     * @return byte[]
     */
    public byte[] receive() {
        return receive(-1);
    }

    /**
     * Aguarda e recebe dados
     *
     * @param timeout
     * @return byte[]
     */
    public byte[] receive(int timeout) {
        try {
            synchronized (lock) {
                if (timeout >= 0) {
                    lock.wait(timeout);
                } else {
                    lock.wait();
                }
                return readBytes;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new byte[]{};
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            List<Package> packages = new LinkedList<>();
            try {
                while (true) {
                    byte[] bytes = new byte[Package.PACKAGE_SIZE];
                    port.getInputStream().read(bytes);
                    Package pack = Package.fromRaw(bytes);
                    System.out.println("Package received");
                    System.out.println("  Part: " + pack.getPart());
                    System.out.println("  PtCt: " + pack.getPartCount());
                    System.out.println("  Data: " + toString(pack.getData()));
                    System.out.println("  CRC : " + pack.getCrc());
                    if (pack.getPart() != packages.size()) {
                        System.out.println("INVALID PACKAGE INDEX");
                    }
                    if (!CRC.check(pack)) {
                        System.out.println("INVALID CRC EXPECTING " + CRC.get(pack));
                    }
                    packages.add(pack);
                    if (pack.getPart() == pack.getPartCount() - 1) {
                        break;
                    }
                }
                readBytes = Package.restore(packages.toArray(new Package[]{}));
                
                System.out.println("String read: '" + new String(readBytes) + "'");
                
                synchronized(lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    String toString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte c : b) {
            sb.append(c).append(' ');
        }
        return sb.toString();
    }
    

}
