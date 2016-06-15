package com.jouwee.comunicacao;

import com.jouwee.comunicacao.comm.Package;
import com.jouwee.comunicacao.comm.PackageListener;
import com.jouwee.comunicacao.comm.SerialComm;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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
        add(buildPanelPacotes());
        add(buildPanelEstatisticas(), BorderLayout.WEST);
    }
    
    public JComponent buildPanelPacotes() {
        JTable table = new JTable();
        TheModel model = new TheModel();
        comm.addListener(model);
        table.setModel(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(400, 60));
        return scroll;
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
    
    private class TheModel implements PackageListener, TableModel {

        private final List<TableModelListener> listeners = new ArrayList<>();
        private final List<PackageInfo> packages = new ArrayList<>();
        
        @Override
        public void packageSent(final Package pack) {
            SwingUtilities.invokeLater(() -> {

                packages.add(new PackageInfo(">", pack));
                for (TableModelListener listener : listeners) {
                    listener.tableChanged(new TableModelEvent(this, packages.size() - 2));
                }
            });
        }

        @Override
        public void packageReceived(final Package pack) {
            SwingUtilities.invokeLater(() -> {
                packages.add(new PackageInfo("<", pack));
                for (TableModelListener listener : listeners) {
                    listener.tableChanged(new TableModelEvent(this, packages.size() - 2));
                }
            });
        }

        @Override
        public int getRowCount() {
            return packages.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return new String[]{
                "T",
                "Dados"
            }[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PackageInfo info = packages.get(rowIndex);
            if (columnIndex == 0) {
                return info.type;
            }
            if (columnIndex == 1) {
                return new String(info.pack.getData());
            }
            return "";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }
        
    }
    
    private class PackageInfo {
        
        private String type;
        private Package pack;

        public PackageInfo(String type, Package pack) {
            this.type = type;
            this.pack = pack;
        }
        
    }
    
    
}
