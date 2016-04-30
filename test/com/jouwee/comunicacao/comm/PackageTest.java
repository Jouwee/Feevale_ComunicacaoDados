/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jouwee.comunicacao.comm;

import org.junit.Test;

/**
 *
 * @author Nicolas
 */
public class PackageTest {
    
    @Test
    public void testFromRaw() {

    }

    @Test
    public void testFromData() {

    }

    @Test
    public void testGetRaw() {
        Package p = new Package(0, 1, new byte[] {1, 2}, 9);
        for (byte b : p.getRaw()) {
            System.out.println(b);          
        }
        System.out.println(p.getRaw());
        
        Package p2 = Package.fromRaw(p.getRaw());
        
        System.out.println(p2.getPart());
        System.out.println(p2.getPartCount());
        System.out.println(p2.getData());
        System.out.println(p2.getCrc());
        
    }

    
}
