package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Panel de monitoramento da porta serial
 * 
 * @author Nicolas Pohren
 */
public class PanelMonitoramentoSerial extends JPanel {
    
    private final Game game;
    private final SerialComm comm;
    private JLabel lUpdateRate;

    public PanelMonitoramentoSerial(Game game, SerialComm comm) {
        super();
        this.comm = comm;
        this.game = game;
        Updater updater = new Updater();
        game.addGameSynchedListener(updater);
        initGui();
        updater.update();
    }
    
    private void initGui() {
        setBorder(BorderFactory.createTitledBorder("Monitoramento serial"));
        setLayout(new BorderLayout());
        add(buildPanelEstatisticas(), BorderLayout.WEST);
    }
    
    private JComponent buildPanelEstatisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(buildLabelUpdateRate());
        return panel;
    }
    
    private JComponent buildLabelUpdateRate() {
        lUpdateRate = new JLabel();
        return lUpdateRate;
    }
        
    private class Updater implements GameSynchedListener {

        private long lastUpdate = System.currentTimeMillis();
        private int gameSynchs = 0;
        
        public Updater() {
            Thread t = new Thread(() -> {
                try {
                    while(true) {
                        Thread.sleep(1000 - (lastUpdate - System.currentTimeMillis()));
                        update();
                        lastUpdate = System.currentTimeMillis();
                    }
                } catch(Exception e) {}
            });
            t.setDaemon(true);
            t.start();
        }
        
        @Override
        public void gameSynched() {
            gameSynchs++;
        }
        
        public void update() {
            final int fGameSynchs = gameSynchs;
            SwingUtilities.invokeLater(() -> {
                lUpdateRate.setText("Taxa sincronização: " + fGameSynchs + "/s");
            });            
            gameSynchs=0;
        }
        
    }
    
    
}
