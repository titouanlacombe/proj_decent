package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import sim.Controller;
import sim.Simulator;
import config.Config;
import utils.Logger;

import java.awt.*;

public class MainWindow extends JFrame {
    JFrame window;
    Config config;
    Simulator sim;
    Logger logger;

    public MainWindow(JFrame window, Config config, Simulator sim) throws Exception {
        this.window = window;
        this.config = config;
        this.sim = sim;
        this.logger = Logger.fileLogger("MainWindow", "./data/ui.log");
    }

    public void initWindow() throws Exception {

        // add window listener to handle window closing without exiting the program
        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {

                // kill the nodes
                if (sim != null) {
                    sim.killNodes();
                }

                // destroy the window
                window.dispose();

                System.exit(0);

            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

        });
    }

    public void startWindow() {
        JLabel startingLabel = new JLabel("Starting");
        startingLabel.setBounds(50, 20, 200, 30);
        this.window.add(startingLabel);

        this.window.setLayout(null);
        this.window.setVisible(true);

        // Update text after 2 seconds
        Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int nbDots = (startingLabel.getText().length() - 8 + 1) % 4;
                String dots = "";
                for (int i = 0; i < nbDots; i++) {
                    dots += ".";
                }
                startingLabel.setText("Starting" + dots);
            }
        });

        timer.setRepeats(true);
        timer.start();
    }

    public void configWindow() {

        ArrayList<String> keys = config.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            JTextField keyTf = new JTextField();
            keyTf.setBounds(240, 30 * (i + 1), 100, 30);
            Object keyValue;
            try {
                keyValue = config.get(keys.get(i));
            } catch (Exception e) {
                keyValue = "Error";
            }
            String keyString = keyValue.toString();
            keyTf.setText(keyString);
            JLabel keyLabel = new JLabel(keys.get(i));
            keyLabel.setBounds(50, 30 * (i + 1), 200, 30);
            keyLabel.setForeground(Color.WHITE);

            this.window.add(keyLabel);
            this.window.add(keyTf);
        }

        // "OK" button
        JButton okButton = new JButton("OK");
        okButton.setBounds(50, (keys.size() + 1) * 30, 100, 30);
        okButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        // get components to find the right textfields
                        Component[] components = window.getContentPane().getComponents();

                        for (int i = 0; i < components.length; i++) {
                            System.out.println(components[i]);
                        }

                        for (int i = 0; i < components.length; i++) {
                            if ((components[i] instanceof JLabel)
                                    && keys.contains(((JLabel) components[i]).getText())) {
                                System.out.println(components[i]);
                                String key = ((JLabel) components[i]).getText();
                                System.out.println("Key : " + key);
                                String value = ((JTextField) components[i + 1]).getText();
                                try {
                                    Field field = config.getField(key);
                                    if (field.getType().isAssignableFrom(int.class)) {
                                        config.set(key, Integer.valueOf(value));
                                    } else if (field.getType().isAssignableFrom(double.class)) {
                                        config.set(key, Double.valueOf(value));
                                    } else if (field.getType().isAssignableFrom(boolean.class)) {
                                        config.set(key, Boolean.valueOf(value));
                                    } else if (field.getType().isAssignableFrom(String.class)) {
                                        config.set(key, value);
                                    }

                                } catch (Exception err) {
                                    logger.error("Error when trying to modify config : \n" + err);
                                }
                            }
                        }

                        clearWindow();

                        System.out.println("Starting simulation with " + config);

                        startWindow();
                        try {
                            sim = new Simulator(config);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            sim.start();
                            clearWindow();
                            simulationWindow(config.numNodes);
                            sim.startSim();
                            updateSimulation();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        System.out.println("Done");
                    }
                });

        this.window.add(okButton);

        this.window.setLayout(null);
        this.window.setVisible(true);
    }

    public void simulationWindow(int nbNodes) {
        // "Simulation..."

        clearWindow();

        // simple GridBagLayout with 2 rows and nbNodes+1 columns
        // 1st column: labels for total and each node (20% of height)
        // 2nd column: progress bars for total or info about entering and leaving (80%
        // of height)
        // margin of 5px on all sides

        GridBagLayout layout = new GridBagLayout();

        this.window.setLayout(layout);

        GridBagConstraints cLabels = new GridBagConstraints();
        cLabels.fill = GridBagConstraints.BOTH;
        cLabels.anchor = GridBagConstraints.CENTER;
        cLabels.insets = new Insets(5, 5, 5, 5);

        cLabels.weightx = 0.5;
        cLabels.weighty = 0.5;

        // total label
        SimLabel totalLabel = new SimLabel("Population");
        cLabels.gridx = 0;
        cLabels.gridy = 0;
        this.window.add(totalLabel, cLabels);

        // node labels
        for (int i = 1; i <= nbNodes; i++) {
            cLabels.gridy = i + 1;

            SimLabel nodeLabel = new SimLabel("Controleur " + i);
            cLabels.gridx = 0;

            this.window.add(nodeLabel, cLabels);

            // node infos
            // label "E :" + entering
            SimLabel enteringLabel = new SimLabel("Veut entrer : ");
            cLabels.gridx = 1;

            this.window.add(enteringLabel, cLabels);
            SimLabel enteringValue = new SimLabel("0");
            cLabels.gridx = 2;
            this.window.add(enteringValue, cLabels);
            // label "S :" + leaving
            SimLabel leavingLabel = new SimLabel("Veut sortir : ");
            cLabels.gridx = 3;
            this.window.add(leavingLabel, cLabels);

            SimLabel leavingValue = new SimLabel("0");
            cLabels.gridx = 4;
            this.window.add(leavingValue, cLabels);
        }

        GridBagConstraints c = cLabels;

        // total progress bar
        SimProgBar totalBar = new SimProgBar(config.roomCapacity);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 4;
        c.weightx = 0.8;

        this.window.add(totalBar, c);

        this.window.setVisible(true);

    }

    public void updateSimulation() {

        // get all the keys for the controllers
        ArrayList<String> keys = new ArrayList<>(sim.getRoom().getControllers().keySet());

        // new Thread
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    // get leaving and entering from Controllers inside sim's Room
                    HashMap<String, Controller> controllers = sim.getRoom().getControllers();

                    // get Nodes
                    // ArrayList<Node> nodes = sim.getRoom().getNodes();
                    int total = 0;
                    // fori
                    for (int i = 0; i < keys.size(); i++) {
                        Controller c = controllers.get(keys.get(i));
                        int leaving = c.get_leaving();
                        int entering = c.get_entering();
                        // update node i
                        updateNode(i + 1, entering, leaving, keys.get(i).equals(sim.getActiveUuid()));

                        total += leaving;

                    }

                    // update total
                    total += sim.getRoom().getNumber();
                    updateBar(total);

                    // sleep 5 seconds
                    try {
                        Thread.sleep(config.uiRefreshInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();

    }

    public void updateNode(int i, int entering, int leaving, boolean active) {

        // get all components
        Component[] components = this.window.getContentPane().getComponents();

        // get node index
        int nodeLabelIndex = 1 + (i - 1) * 5;

        if (active) {
            for (int j = nodeLabelIndex; j < nodeLabelIndex + 5; j++) {
                // change color to yellow
                JLabel nodeLabel = (JLabel) components[j];
                nodeLabel.setForeground(Color.YELLOW);
            }

        } else {
            for (int j = nodeLabelIndex; j < nodeLabelIndex + 5; j++) {
                // change color to white
                JLabel nodeLabel = (JLabel) components[j];
                nodeLabel.setForeground(Color.WHITE);
            }
        }

        // update entering
        JLabel enteringValue = (JLabel) components[nodeLabelIndex + 2];
        enteringValue.setText(Integer.toString(entering));
        // change color to green if entering > 0
        if (entering > 0) {
            enteringValue.setForeground(Color.GREEN);
        } else {
            enteringValue.setForeground(Color.WHITE);
        }

        // update leaving
        JLabel leavingValue = (JLabel) components[nodeLabelIndex + 4];
        leavingValue.setText(Integer.toString(leaving));
        // change color to red if leaving > 0
        if (leaving > 0) {
            leavingValue.setForeground(Color.RED);
        } else {
            leavingValue.setForeground(Color.WHITE);
        }

        // update window
        this.window.validate();
        this.window.repaint();
    }

    public void updateBar(int value) {
        // update progress bar for node
        // update total progress bar

        // get all components
        Component[] components = this.window.getContentPane().getComponents();

        // total progress bar index
        int totalBarIndex = components.length - 1;

        // update total progress bar
        JProgressBar totalBar = (JProgressBar) components[totalBarIndex];
        totalBar.setValue(value);
        totalBar.setString(Integer.toString(value) + " / " + totalBar.getMaximum() + " ("
                + Integer.toString(value * 100 / totalBar.getMaximum()) + "%)");

        // update window
        this.window.validate();
        this.window.repaint();

    }

    public void clearWindow() {
        Container contenu = this.window.getContentPane();

        // Supprimer tous les composants du conteneur
        contenu.removeAll();

        // Mettre à jour la fenêtre pour refléter les changements
        this.window.validate();
        this.window.repaint();
    }
}
