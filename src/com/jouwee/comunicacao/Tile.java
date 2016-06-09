package com.jouwee.comunicacao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Tile {

    public static final int SIZE = 48;
    private final int id;
    private Color color;
    private boolean solid;

    public Tile(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.fillRect(0, 0, SIZE, SIZE);
        g2d.dispose();
    }
    
}
