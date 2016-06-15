package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Servidor do jogo
 *
 * @author Nícolas Pohren
 */
public class GameServer extends Game {

    /**
     * Cria o servidor 
     * 
     * @param serialComm 
     */
    public GameServer(SerialComm serialComm) {
        super(serialComm);
    }
    
    @Override
    public void sendData() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(getPlayerServer());
            out.writeObject(getBullets());
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
            
            getPlayerClient().setPosition(p1.getX(), p1.getY());
            getPlayerClient().setVector(p1.getVector());
            getPlayerClient().setLife(p1.getLife());
            getPlayerClient().setFacing(p1.getFacing());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
