package com.jouwee.comunicacao;

import java.io.Serializable;

/**
 * Vetor de movimento
 * 
 * @author Nicolas Pohren
 */
public class Vector implements Serializable {

    /** Movimento X */
    private final float x;
    /** Movimento Y */
    private final float y;

    /**
     * Cria um novo vetor de movimento
     */
    public Vector() {
        this(0, 0);
    }
    
    /**
     * Cria um novo vetor de movimento
     * 
     * @param x
     * @param y 
     */
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retorna um novo vetor adicionando os vetores
     * 
     * @param x
     * @param y
     * @return Vector
     */
    public Vector add(float x, float y) {
        return new Vector(this.x + x, this.y + y);
    }
    
    /**
     * Retorna a aceleração X
     * 
     * @return float
     */
    public float getX() {
        return x;
    }

    /**
     * Retorna a aceleração Y
     * 
     * @return float
     */
    public float getY() {
        return y;
    }
    
}
