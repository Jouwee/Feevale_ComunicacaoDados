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

    public static final int KEY_JUMP = 0;
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_SHOOT = 3;
    public static final int[] KEY_SCHEME_0 = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE};
    public static final int[] KEY_SCHEME_1 = {KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD0};
    /** Vetor de movimento do jogador */
    private Vector vector;
    /** Cor do jogador */
    private Color color;
    /** Cor do jogador */
    private int[] keyScheme;
    
    /**
     * Cria um novo jogador
     */
    public Player() {
        setPosition(50, 50);
        vector = new Vector();
    }
    
    @Override
    public void update(UpdateContext context) {
        if (context.isPressed(keyScheme[KEY_JUMP])) {
            vector = vector.add(0, 1);
        } else {
            vector = vector.add(0, -1);
        }
        setX(getX() + vector.getX());
        setY(getY() + vector.getY());
        if (getY() < 0) {
            setY(0);
            vector = new Vector(vector.getX(), 0);
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.fillRect((int) getX(), (int) getY(), 50, 50);
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
    
}
