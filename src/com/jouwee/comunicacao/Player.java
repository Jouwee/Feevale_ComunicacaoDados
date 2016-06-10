package com.jouwee.comunicacao;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Jogador
 * 
 * @author Nicolas Pohren
 */
public class Player extends Entity {

    public static final float SPEED = 0.02f;
    public static final int SIZE = 48;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int KEY_JUMP = 0;
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_SHOOT = 3;
    public static final int[] KEY_SCHEME_0 = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE};
    public static final int[] KEY_SCHEME_1 = {KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_SPACE};
    /** Vetor de movimento do jogador */
    private Vector vector;
    /** Cor do jogador */
    private Color color;
    /** Cor do jogador */
    private int[] keyScheme;
    private int facing;
    
    /**
     * Cria um novo jogador
     */
    public Player() {
        setPosition(50, 50);
        vector = new Vector();
    }
    
    @Override
    public void update(UpdateContext context) {
        float friction = 0.7f;
        if (context.isPressed(keyScheme[KEY_LEFT])) {
            vector = vector.add((float) -SPEED, 0);
            friction = 1;
            setFacing(LEFT);
        }
        if (context.isPressed(keyScheme[KEY_RIGHT])) {
            vector = vector.add((float) SPEED, 0);
            friction = 1;
            setFacing(RIGHT);
        }
        if (context.isPressed(keyScheme[KEY_SHOOT])) {
            Bullet bullet = new Bullet();
            if (facing == LEFT) {
                bullet.setX(getX());
            } else {
                bullet.setX(getX()  + 1);
            }
            bullet.setY(getY() + 0.5f);
            bullet.setDirection(facing);
            context.getGame().addBullet(bullet);
        }
        if (context.isPressed(keyScheme[KEY_JUMP]) && isContactBelow(context)) {
            vector = vector.add(0, (float) -.4);
        } else {
            vector = vector.add(0, (float) .027);
        }
        setX(getX() + vector.getX());
        setY(getY() + vector.getY());
        if (isContactBelow(context) || isContactAbove(context)) {
            setY((float) Math.round(getY()));
            vector = new Vector(vector.getX() * friction, 0);
        }
        if (isContactLeft(context) || isContactRight(context)) {
            setX((float) Math.round(getX()));
            vector = new Vector(0, vector.getY());
        }
    }
    
    public boolean isContactBelow(UpdateContext context) {
        int ix, iy;
        iy = (int) getY() + 1;
        if (iy - getY() > 0.5 && iy - getY() < -.05) {
            iy++;
        }
        ix = (int) Math.floor(getX() + 0.1);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        
        ix = (int) Math.floor(getX() + 0.9);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        return false;
    }
    
    public boolean isContactAbove(UpdateContext context) {
        int ix, iy;
        iy = (int) getY() ;
        if (iy - getY() > 0.5 && iy - getY() < -.05) {
            iy++;
        }
        ix = (int) Math.floor(getX() + 0.1);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        ix = (int) Math.floor(getX() + 0.9);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        return false;
    }

    public boolean isContactLeft(UpdateContext context) {
        int ix, iy;
        ix = (int) getX() ;
        if (ix - getX() > 0.5 && ix - getX() < -.05) {
            ix++;
        }
        iy = (int) Math.floor(getY() + 0.1);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        iy = (int) Math.floor(getY() + 0.9);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        return false;
    }

    public boolean isContactRight(UpdateContext context) {
        int ix, iy;
        ix = (int) getX() + 1;
        if (ix - getX() > 0.5 && ix - getX() < -.05) {
            ix++;
        }
        iy = (int) Math.floor(getY() + 0.1);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        iy = (int) Math.floor(getY() + 0.9);
        if (context.getGame().getMap().getTile(ix, iy).isSolid()) {
            return true;
        }
        return false;
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.fillRect((int) (getX() * SIZE), (int) (getY() * SIZE), SIZE, SIZE);
        g2d.setColor(Color.GREEN);
        if (facing == RIGHT) {
            g2d.fillRect((int) (getX() * SIZE) + SIZE, (int) (getY() * SIZE) + SIZE/2, 3, 3);
        } else {
            g2d.fillRect((int) (getX() * SIZE) - 3, (int) (getY() * SIZE) + SIZE/2, 3, 3);
        }
        g2d.dispose();
    }

    /**
     * Retorna o vetor
     * 
     * @return Vector
     */
    public Vector getVector() {
        return vector;
    }

    /**
     * Define o vetor
     * 
     * @param vector 
     */
    public void setVector(Vector vector) {
        this.vector = vector;
    }

    /**
     * Retorna a cor do jogador
     * 
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Define a cor do jogador
     * 
     * @param color 
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Retorna o esquema de controles do jogador
     * 
     * @return int[]
     */
    public int[] getKeyScheme() {
        return keyScheme;
    }

    /**
     * Define o esquema de controles do jogador
     * 
     * @param keyScheme 
     */
    public void setKeyScheme(int[] keyScheme) {
        this.keyScheme = keyScheme;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
    
}
