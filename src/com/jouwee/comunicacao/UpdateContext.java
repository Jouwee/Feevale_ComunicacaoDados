/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jouwee.comunicacao;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nicolas
 */
public class UpdateContext implements KeyEventDispatcher {
   
    /** Conjunto de teclas preesionadas */
    private final Set<Integer> pressedKeys;
    /** Mapa */
    private GameMap map;

    /**
     * Cria novo contexto de atualização
     */
    public UpdateContext() {
        this.pressedKeys = new HashSet<>();
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }
    
    /**
     * Define uma tecla como pressionada
     * 
     * @param keyCode 
     */
    public void setPressed(int keyCode) {
        pressedKeys.add(keyCode);
    }
    
    /**
     * Remove uma tecla do conjunto de pressionadas
     * 
     * @param keyCode 
     */
    public void removePressed(int keyCode) {
        pressedKeys.remove(keyCode);
    }
    
    /**
     * Retorna se uma tecla está pressionada
     * 
     * @param keyCode
     * @return boolean
     */
    public boolean isPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            setPressed(e.getKeyCode());
        }
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            removePressed(e.getKeyCode());
        }
        return false;
    }
    
}
