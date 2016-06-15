package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

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
            List<Bullet> bullets = (List<Bullet>) ois.readObject();
            
            getPlayerServer().setPosition(p1.getX(), p1.getY());
            getPlayerServer().setVector(p1.getVector());
            getPlayerServer().setLife(p1.getLife());
            getPlayerServer().setFacing(p1.getFacing());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}