package com.jouwee.comunicacao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nicolas Pohren
 */
public class GameMap {
    
    private static final Map<Integer, Tile> PROTOTYPES = new HashMap<>();
    static {
        Tile air = new Tile(0xFFFFFFFF);
        air.setColor(Color.WHITE);
        PROTOTYPES.put(air.getId(), air);
        Tile obstacle = new Tile(0xFF000000);
        obstacle.setColor(Color.BLACK);
        obstacle.setSolid(true);
        PROTOTYPES.put(obstacle.getId(), obstacle);
    }
    
    /** Tiles */
    private int[][] tiles;

    public GameMap() {
        tiles = new int[0][0];
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                Graphics2D internal = (Graphics2D) g2d.create();
                internal.translate(x * Tile.SIZE, y * Tile.SIZE);
                getTile(x, y).render(internal);
                internal.dispose();
            }
        }
        g2d.dispose();
    }
    
    public Tile getTile(int x, int y) {
        Tile t = null;
        if (x >= 0 && y >= 0 && x < tiles.length && y < tiles[0].length) {
            t = PROTOTYPES.get(tiles[x][y]);
        }
        if (t == null) {
            t = PROTOTYPES.get(0xFFFFFFFF);
        }
        return t;
    }
    
    public int[][] getTiles() {
        return tiles;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }
    
}
