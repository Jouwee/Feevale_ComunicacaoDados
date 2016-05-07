package com.jouwee.comunicacao;

import java.awt.Graphics;
import java.io.Serializable;

/**
 * Entidade do jogo
 * 
 * @author Nícolas
 */
public abstract class Entity implements Serializable {
    
    /** Posição X */
    private float x;
    /** Posição Y */
    private float y;
    
    /**
     * Atualiza a entidade
     * 
     * @param context 
     */
    public abstract void update(UpdateContext context);
    
    /**
     * Renderiza a entidade
     * 
     * @param g 
     */
    public abstract void render(Graphics g);

    /**
     * Define a posição da entidade
     * 
     * @param x
     * @param y 
     */
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }
    
    /**
     * Retorna a posição X
     * 
     * @return float
     */
    public float getX() {
        return x;
    }

    /**
     * Define a posição X
     * 
     * @param x 
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Retorna a posição Y
     * 
     * @return float
     */
    public float getY() {
        return y;
    }

    /**
     * Define a posição Y
     * 
     * @param y 
     */
    public void setY(float y) {
        this.y = y;
    }
    
}
