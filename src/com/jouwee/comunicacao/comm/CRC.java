package com.jouwee.comunicacao.comm;

import java.util.Random;

/**
 * Calculador de CRC
 * 
 * @author Nicolas
 */
public class CRC {
    
    /**
     * Gera CRC para um pacote
     * 
     * @param pack
     * @return long
     */
    public static long get(Package pack) {
        return get(pack.getPart(), pack.getPartCount(), pack.getData());
    }
    
    /**
     * Gera CRC para um pacote
     * 
     * @param part
     * @param partCount
     * @param data
     * @return long
     */
    public static long get(int part, int partCount, byte[] data) {
        Random random1 = new Random(part);
        Random random2 = new Random(partCount + random1.nextInt());
        long crc = 0;
        for (int i = 0; i < data.length; i++) {
            crc += (random1.nextInt() * data[i]) + (random2.nextInt() * data[i]);
        }
        return crc;
    }
    
    /**
     * Verifica o CRC de um pacote
     * 
     * @param pack
     * @return boolean
     */
    public static boolean check(Package pack) {
        return get(pack) == pack.getCrc();
    }
    
}
