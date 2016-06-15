package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 * Classe com o estado do jogo
 * 
 * @author Nícolas Pohren
 */
public abstract class Game {

    private final EventListenerList listenerList = new EventListenerList();
    /** Comunicação serial */
    private final SerialComm serialComm;
    /** Repainter */
    private Runnable repainter;
    /** Jogador do servidor */
    private Player playerServer;
    /** Jogador do cliente */
    private Player playerClient;
    private List<Bullet> bullets;
    /** Mapa */
    private GameMap map;
    /** Contexto de atualização do jogo */
    private final UpdateContext context;

    /**
     * Cria um novo jogo
     * 
     * @param serialComm
     */
    public Game(SerialComm serialComm) {
        context = new UpdateContext();
        bullets = new ArrayList<>();
        this.serialComm = serialComm;
    }
    
    /**
     * Inicia o loop de jogo em uma thread separada
     */
    public void startGameLoop() {
        Thread gameThread = new Thread(this::gameLoop, "Game thread");
        gameThread.setDaemon(true);
        gameThread.start();
        Thread synch = new Thread(this::synchLoop, "Data send thread");
        synch.setDaemon(true);
        synch.start();
    }
    
    /**
     * Executa o jogo
     */
    public void gameLoop() {
        init();
        try {
            while (true) {
                update();
                repainter.run();
                Thread.sleep(30);
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    
    /**
     * Executa o jogo
     */
    public void synchLoop() {
        init();
        try {
            while (true) {
                sendData();
                receiveData();
//                Thread.sleep(30);
                fireGameSynched();
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    
    /**
     * Inicializa o jogo
     */
    public void init() {
        playerServer = new Player();
        playerServer.setPosition(2, 2);
        playerServer.setColor(Color.RED);
        playerServer.setKeyScheme(Player.KEY_SCHEME_0);
        playerClient = new Player();
        playerClient.setPosition(4, 2);
        playerClient.setColor(Color.BLUE);
        playerClient.setKeyScheme(Player.KEY_SCHEME_1);
        map = new GameMap();
        MapLoader.load(map, "map.png");
        context.setGame(this);
    }
    
    /**
     * Atualiza o estado do jogo
     */
    public void update() {
        playerServer.update(context);
        playerClient.update(context);
        for (Bullet bullet : new ArrayList<>(bullets)) {
            bullet.update(context);
        }
    }
    
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }
    
    public void removeBullet(Bullet bullet) {
        bullets.remove(bullet);
    }
    
    /**
     * Sincroniza o estado do jogo
     */
    public abstract void sendData();
    
    /**
     * Sincroniza o estado do jogo
     */
    public abstract void receiveData();
    
    /**
     * Renderiza o jogo
     * 
     * @param g 
     */
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        g2d.fill(g2d.getClip());
        if (map != null) {
            map.render(g2d);
        }
        for (Bullet bullet : new ArrayList<>(bullets)) {
            bullet.render(g2d);
        }
        if (playerServer != null) {
            playerServer.render(g2d);
            playerClient.render(g2d);
        }         
        renderGui(g2d);
        g2d.dispose();
    }
    
    public void renderGui(Graphics g) {
        int barSize = 200;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        int h = g2d.getClipBounds().height;
        int w = g2d.getClipBounds().width;
        //
        g2d.setColor(playerServer.getColor());
        float lifePct = (float)playerServer.getLife() / Player.MAX_HEALTH;
        g2d.fillPolygon(new int[] {5, 5 + (int)(barSize * lifePct), 5 + (int)(barSize * lifePct)-15, 5}, new int[] {h - 30, h - 30, h - 5, h - 5}, 4);
        g2d.setColor(Color.WHITE);
        g2d.drawPolygon(new int[] {5, 5 + barSize, 5 + barSize-15, 5}, new int[] {h - 30, h - 30, h - 5, h - 5}, 4);
        //
        g2d.setColor(playerClient.getColor());
        lifePct = (float)playerClient.getLife() / Player.MAX_HEALTH;
        g2d.fillPolygon(new int[] {w - 5, w - 5 - (int)(barSize * lifePct), w - 5 - (int)(barSize * lifePct)-15, w - 5}, new int[] {h - 30, h - 30, h - 5, h - 5}, 4);
        g2d.setColor(Color.WHITE);
        g2d.drawPolygon(new int[] {w - 5, w - 5 - barSize, w - 5 - barSize-15, w - 5}, new int[] {h - 30, h - 30, h - 5, h - 5}, 4);
        g2d.dispose();
    }
    
    public void addGameSynchedListener(GameSynchedListener l) {
        listenerList.add(GameSynchedListener.class, l);
    }
    
    public void fireGameSynched() {
        for (GameSynchedListener listener : listenerList.getListeners(GameSynchedListener.class)) {
            listener.gameSynched();
        }
    }

    /**
     * Retorna o requisitor de repaint
     * 
     * @return Runnable
     */
    public Runnable getRepainter() {
        return repainter;
    }

    /**
     * Defnie o requisitor de repaints
     * 
     * @param repainter 
     */
    public void setRepainter(Runnable repainter) {
        this.repainter = repainter;
    }

    /**
     * Retorna o contexto de atualização do jogo
     * 
     * @return UpdateContext
     */
    public UpdateContext getContext() {
        return context;
    }

    /**
     * Retorna o jogador do servidor
     * 
     * @return Player
     */
    public Player getPlayerServer() {
        return playerServer;
    }

    /**
     * Retorna o jogador do cliente
     * 
     * @return Player
     */
    public Player getPlayerClient() {
        return playerClient;
    }
    
    /**
     * Retorna a porta de comunicação serial
     * 
     * @return SerialComm
     */
    public SerialComm getSerialComm() {
        return serialComm;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public GameMap getMap() {
        return map;
    }

}
