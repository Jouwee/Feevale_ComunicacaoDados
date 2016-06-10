package com.jouwee.comunicacao;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import javax.swing.JPanel;

/**
 *
 * @author Nícolas
 */
public class GamePanel extends JPanel {

    /** Jogo */
    private final Game game;

    /**
     * Game game
     *
     * @param game
     */
    public GamePanel(Game game) {
        this.game = game;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(game.getContext());
        // Método a chamar para requisitar um repaint
        game.setRepainter(this::repaint);
        setPreferredSize(new Dimension(24*Tile.SIZE, 12*Tile.SIZE));
    }

    @Override
    public void paint(Graphics g) {
        game.render(g);
    }

}
