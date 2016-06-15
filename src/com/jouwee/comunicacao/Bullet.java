package com.jouwee.comunicacao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author Nicolas
 */
public class Bullet extends Entity {

    private static final int DAMAGE = 5;
    private static final float SPEED = 1f;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public int direction;
    private Player owner;
    
    @Override
    public void update(UpdateContext context) {
        float oldX = getX();
        if (direction == RIGHT) {
            setX(getX()+SPEED);
        } else {
            setX(getX()-SPEED);
        }
        if (getX() < 0 || getX() > context.getGame().getMap().getWidth()) {
            context.getGame().removeBullet(this);
        }
        if (context.getGame().getMap().getTile((int) getX(), (int) getY()).isSolid()) {
            context.getGame().removeBullet(this);
        }
        if (context.getGame().getPlayerClient() == owner) {
            testCollision(context, context.getGame().getPlayerServer(), oldX);
        } else {
            testCollision(context, context.getGame().getPlayerClient(), oldX);
        }
        
    }
    
    public void testCollision(UpdateContext context, Player player, float oldX) {
        if (getY() < player.getY() || getY() > player.getY() + 1) {
            return;
        }
        if (getX() < player.getX() || getX() > player.getX() + 1) {
            float menorX = Math.min(oldX, getX());
            float maiorX = Math.max(oldX, getX());
            if (!(menorX < player.getX() && maiorX > player.getX())) {
                return;
            }
        }
        player.setLife(player.getLife() - DAMAGE);
        context.getGame().removeBullet(this);
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int)(getX() * Tile.SIZE) - 3, (int)(getY() * Tile.SIZE) - 3, 6, 6);
        g2d.dispose();
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
    
}
