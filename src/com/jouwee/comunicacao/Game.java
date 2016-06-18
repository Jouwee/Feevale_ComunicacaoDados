package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.Package;
import com.jouwee.comunicacao.comm.PackageListener;
import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
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
    private GameStatus status;
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
        try {
            serialComm.addListener(new PackageListener() {
                @Override public void packageSent(Package pack) { }
                @Override public void packageReceived(Package pack) { }

                @Override
                public void bytesRead(byte[] bytes) {
                    dataReceived(bytes);
                }
            });
            while (true) {
                sendData();
                Thread.sleep(1000 / 30);
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
        status = GameStatus.RUNNING;
    }
    
    /**
     * Atualiza o estado do jogo
     */
    public void update() {
        if (status != GameStatus.RUNNING) {
            return;
        }
        playerServer.update(context);
        playerClient.update(context);
        for (Bullet bullet : new ArrayList<>(bullets)) {
            bullet.update(context);
        }
        if (getPlayerServer().getLife() == 0 && getPlayerClient().getLife() == 0) {
            status = GameStatus.OVER_TIE;
        } else {
            if (getPlayerServer().getLife() == 0) {
                status = GameStatus.OVER_WINNER_CLIENT;
            }
            if (getPlayerClient().getLife() == 0) {
                status = GameStatus.OVER_WINNER_SERVER;
            }
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
     * 
     * @param bytes
     */
    public abstract void dataReceived(byte[] bytes);
    
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
        if (status != GameStatus.RUNNING) {
            renderGameOver(g2d);
        }
        g2d.dispose();
    }
    
    public void renderGameOver(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Color background = null;
        Color foreground = null;
        String text = null;
        if (status == GameStatus.OVER_TIE) {
            background = new Color(0x80CCCCCC, true);
            foreground = Color.BLACK;
            text = "Empate";
        }
        if (status == GameStatus.OVER_WINNER_CLIENT) {
            background = new Color(getPlayerClient().getColor().brighter().brighter().getRGB() & 0x50FFFFFF, true);
            foreground = getPlayerClient().getColor().darker().darker();
            text = "Jogador CLIENT ganhou!";
        }
        if (status == GameStatus.OVER_WINNER_SERVER) {
            background = new Color(getPlayerServer().getColor().brighter().brighter().getRGB() & 0x50FFFFFF, true);
            foreground = getPlayerServer().getColor().darker().darker();
            text = "Jogador SERVER ganhou!";
        }
        g2d.setColor(background);
        g2d.fill(g2d.getClip());
        
        Font fonte = new Font("Calibri", Font.BOLD, 60);
        g2d.setFont(fonte);
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(fonte);
        
        String gameOverText = "GAME OVER";
        Rectangle2D rect = fm.getStringBounds(gameOverText, g);
        
        float w = (float) g2d.getClip().getBounds().getWidth();
        
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(background);
        g2d.fill(g2d.getClip());

        g2d.setColor(foreground);
        g2d.drawString(gameOverText, (int) ((w / 2) - (rect.getWidth()/ 2)), 250);
        rect = fm.getStringBounds(text, g);
        g2d.drawString(text, (int) ((w / 2) - (rect.getWidth() / 2)), 350);

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

    public List<Bullet> getBullets(Player player) {
        List<Bullet>  ret = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (bullet.getOwner() == player) {
                ret.add(bullet);
            }
        }
        return ret;
    }
    
    public void synchBullets(List<Bullet>  toSynch, Player player) {
        bullets.removeAll(getBullets(player));
        bullets.addAll(toSynch);
    }

    public GameMap getMap() {
        return map;
    }

}
