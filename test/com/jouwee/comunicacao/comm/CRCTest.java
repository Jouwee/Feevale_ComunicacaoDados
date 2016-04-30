package com.jouwee.comunicacao.comm;

import org.junit.Test;

/**
 * Testes unitários do gerador de CRC
 * 
 * @author Nicolas
 */
public class CRCTest {
    
    /**
     * Test of get method, of class CRC.
     */
    @Test
    public void testGet() {
        System.out.println(CRC.get(1, 1, new byte[] {}));
        System.out.println(CRC.get(1, 1, new byte[] {1}));
        System.out.println(CRC.get(1, 1, new byte[] {1, 2}));
        System.out.println(CRC.get(1, 2, new byte[] {1, 2}));
        System.out.println(CRC.get(2, 1, new byte[] {1, 2}));
        System.out.println(CRC.get(2, 1, new byte[] {2, 1}));
        
        System.out.println("");
        System.out.println(CRC.get(new Package(2, 1, new byte[] {2, 1}, 8)));
        
    }
    
}
