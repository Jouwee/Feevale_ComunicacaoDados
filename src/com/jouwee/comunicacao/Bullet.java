package com.jouwee.comunicacao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author Nicolas
 */
public class Bullet extends Entity {

    private static final float SPEED = .3f;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public int direction;
    
    @Override
    public void update(UpdateContext context) {
        if (direction == RIGHT) {
            setX(getX()+SPEED);
        } else {
            setX(getX()-SPEED);
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int)(getX() * Tile.SIZE), (int)(getY() * Tile.SIZE), 3, 3);
        g2d.dispose();
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    
    
}
