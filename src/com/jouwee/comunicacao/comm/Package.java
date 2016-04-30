package com.jouwee.comunicacao.comm;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Pacote de transmissão
 * 
 * @author Nícolas
 */
public class Package {
    
    /** Tamanho de um pacote */
    public static final int FRAME_SIZE = 128;
    /** Tamanho de um pacote */
    public static final int PACKAGE_SIZE = FRAME_SIZE + 4 + 4 + 8;
    /** Parte */
    private final int part;
    /** Número de partes */
    private final int partCount;
    /** Dados do pacote */
    private final byte[] data;
    /** CRC do pacote */
    private final long crc;

    /**
     * Cria novo pacote
     * 
     * @param part
     * @param partCount
     * @param data
     * @param crc 
     */
    public Package(int part, int partCount, byte[] data, long crc) {
        this.part = part;
        this.partCount = partCount;
        this.data = Arrays.copyOf(data, FRAME_SIZE);
        this.crc = crc;
    }
    
    /**
     * Cria um pacote à partir de dados crus
     * 
     * @param raw
     * @return Package
     */
    public static Package fromRaw(byte[] raw) {
        int part = ByteBuffer.wrap(raw, 0, 4).asIntBuffer().get();
        int partCount = ByteBuffer.wrap(raw, 4, 4).asIntBuffer().get();
        byte[] data = Arrays.copyOfRange(raw, 8, 8 + FRAME_SIZE);
        long crc = ByteBuffer.wrap(raw, 8 + FRAME_SIZE, 8).asLongBuffer().get();
        return new Package(part, partCount, data, crc);
    }
    
    /**
     * Cria um pacote à partir dos dados do pacote
     * 
     * @param data
     * @return Package
     */
    public static Package[] fromData(byte[] data) {
        int offset = 0;
        int part = 0;
        int totalParts = data.length / FRAME_SIZE + 1;
        Package[] packages = new Package[totalParts];
        while (offset < data.length) {
            byte[] theData = Arrays.copyOfRange(data, offset, offset + FRAME_SIZE);
            packages[part] = new Package(part, totalParts, theData, CRC.get(part, totalParts, data));
            part++;
            offset += FRAME_SIZE;
        }
        return packages;
    }
    
    /**
     * Restaura os bytes dos pacotes
     * 
     * @param packages
     * @return byte[]
     */
    public static byte[] restore(Package[] packages) {
        byte[] data = new byte[0];
        for (Package pack : packages) {
            int offset = data.length;
            data = Arrays.copyOf(data, data.length + FRAME_SIZE);
            for (int i = 0; i < pack.getData().length; i++) {
                data[offset + i] = pack.getData()[i];
                
            }
        }
        return data;
    }

    /**
     * Retorna o CRC
     * 
     * @return long
     */
    public long getCrc() {
        return crc;
    }
    
    /**
     * Retorna o índice da parte
     * 
     * @return int
     */
    public int getPart() {
        return part;
    }

    /**
     * Retorna o número de partes
     * 
     * @return int
     */
    public int getPartCount() {
        return partCount;
    }
    
    /**
     * Retorna os dados crus do pacote
     * 
     * @return byte[]
     */
    public byte[] getRaw() {
        byte[] partBytes = ByteBuffer.allocate(4).putInt(part).array();
        byte[] partCountBytes = ByteBuffer.allocate(4).putInt(partCount).array();
        byte[] crcBytes = ByteBuffer.allocate(8).putLong(crc).array();
        byte[] raw = new byte[data.length + partBytes.length + partCountBytes.length + crcBytes.length];
        int offset = 0;
        for (int i = 0; i < partBytes.length; i++) {
            raw[offset + i] = partBytes[i];
        }
        offset += partCountBytes.length;
        for (int i = 0; i < partCountBytes.length; i++) {
            raw[offset + i] = partCountBytes[i];
        }
        offset += partBytes.length;
        for (int i = 0; i < data.length; i++) {
            raw[offset + i] = data[i];
        }
        offset += data.length;
        for (int i = 0; i < crcBytes.length; i++) {
            raw[offset + i] = crcBytes[i];
        }
        return raw;
    }

    /**
     * Retorna os dados do pacote
     * 
     * @return byte[]
     */
    public byte[] getData() {
        return data;
    }
    
}
