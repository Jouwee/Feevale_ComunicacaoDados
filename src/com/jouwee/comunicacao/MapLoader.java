package com.jouwee.comunicacao;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * 
 * 
 * @author Nicolas Pohren
 */
public class MapLoader {
    
    public static void load(GameMap map, String resource) {
        InputStream is = MapLoader.class.getResourceAsStream(resource);
        try {
            BufferedImage image = ImageIO.read(is);
            int[][] tiles = new int[image.getWidth()][image.getHeight()];
            for (int x = 0; x < tiles.length; x++) {
                for (int y = 0; y < tiles[x].length; y++) {
                    tiles[x][y] = image.getRGB(x, y);
                }
            }
            map.setTiles(tiles);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
