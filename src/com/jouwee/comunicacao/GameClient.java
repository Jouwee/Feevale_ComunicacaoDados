/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Nicolas
 */
public class GameClient extends Game {

    /**
     * Cria o servidor 
     * 
     * @param serialComm 
     */
    public GameClient(SerialComm serialComm) {
        super(serialComm);
    }
    
    @Override
    public void sendData() {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(getPlayerClient());
            out.close();
            getSerialComm().send(baos.toByteArray());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveData() {
        try {
            byte[] bytes = getSerialComm().receive();
            
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Player p1 = (Player) ois.readObject();
            
            getPlayerServer().setPosition(p1.getX(), p1.getY());
            getPlayerServer().setVector(p1.getVector());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}