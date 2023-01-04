package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;

import sim.Controller;
import sim.Simulator;
import config.Config;

import java.awt.*;

public class MainWindow extends JFrame {
    JFrame window;
    Config config;
    Simulator sim;

    public MainWindow(JFrame window, Config config, Simulator sim) {
        this.window = window;
        this.config = config;
        this.sim = sim;
    }

    public void initWindow() {
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

        // Display config info
        System.out.println(config);

        // Nodes field
        JTextField nodesTf = new JTextField();
        // label for nodesTf
        JLabel nodesLabel = new JLabel("Nombre de noeuds");
        nodesLabel.setForeground(Color.WHITE);
        // persons field
        JTextField personsTf = new JTextField();
        // label for personsTf
        JLabel personsLabel = new JLabel("Nombre de personnes");
        personsLabel.setForeground(Color.WHITE);

        nodesLabel.setBounds(50, 20, 200, 30);
        nodesTf.setBounds(220, 20, 100, 30);
        nodesTf.setText(Integer.toString(config.numNodes));

        personsLabel.setBounds(50, 60, 200, 30);
        personsTf.setBounds(220, 60, 100, 30);
        personsTf.setText(Integer.toString(config.roomCapacity));

        this.window.add(nodesTf);
        this.window.add(nodesLabel);
        this.window.add(personsTf);
        this.window.add(personsLabel);

        // "OK" button
        JButton okButton = new JButton("OK");
        okButton.setBounds(50, 100, 100, 30);
        okButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // print content of nodesTf and personsTf
                        System.out.println("Starting simulation with " + nodesTf.getText() + " nodes and "
                                + personsTf.getText() + " persons");

                        int numNodes = Integer.parseInt(nodesTf.getText());
                        int numPersons = Integer.parseInt(personsTf.getText());
                        // Config config = new Config(nb_nodes, nb_persons);
                        clearWindow();
                        config.numNodes = numNodes;
                        config.roomCapacity = numPersons;

                        System.out.println(config.toString());

                        startWindow();
                        sim = new Simulator(config);
                        try {
                            sim.start();
                            clearWindow();
                            simulationWindow(config.numNodes);
                            sim.startSim();
                            // start updateSimulation() in a new thread

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

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;

        // total label
        JLabel totalLabel = new JLabel("Population");
        c.gridx = 0;
        c.gridy = 0;
        // c.fill = GridBagConstraints.BOTH;
        // c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 5, 5);
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setVerticalAlignment(SwingConstants.CENTER);
        totalLabel.setForeground(Color.WHITE);
        this.window.add(totalLabel, c);

        // node labels
        for (int i = 1; i <= nbNodes; i++) {
            JLabel nodeLabel = new JLabel("Contrôleur " + i);
            c.gridx = 0;
            c.gridy = i + 1;
            c.weightx = 0.5;
            c.weighty = 0.5;
            // c.fill = GridBagConstraints.BOTH;
            // c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            nodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nodeLabel.setVerticalAlignment(SwingConstants.CENTER);
            nodeLabel.setForeground(Color.WHITE);
            this.window.add(nodeLabel, c);

            // node infos
            // label "E :" + entering
            JLabel enteringLabel = new JLabel("Veut entrer : ");
            c.gridx = 1;
            c.gridy = i + 1;
            // c.fill = GridBagConstraints.BOTH;
            // c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            enteringLabel.setHorizontalAlignment(SwingConstants.CENTER);
            enteringLabel.setVerticalAlignment(SwingConstants.CENTER);
            enteringLabel.setForeground(Color.WHITE);

            this.window.add(enteringLabel, c);
            JLabel enteringValue = new JLabel("0");
            c.gridx = 2;
            c.gridy = i + 1;
            // c.fill = GridBagConstraints.BOTH;
            // c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            enteringValue.setHorizontalAlignment(SwingConstants.CENTER);
            enteringValue.setVerticalAlignment(SwingConstants.CENTER);
            enteringValue.setForeground(Color.WHITE);
            this.window.add(enteringValue, c);
            // label "S :" + leaving
            JLabel leavingLabel = new JLabel("Veut sortir : ");
            c.gridx = 3;
            c.gridy = i + 1;
            // c.fill = GridBagConstraints.BOTH;
            // c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            leavingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leavingLabel.setVerticalAlignment(SwingConstants.CENTER);
            leavingLabel.setForeground(Color.WHITE);
            this.window.add(leavingLabel, c);
            JLabel leavingValue = new JLabel("0");
            c.gridx = 4;
            c.gridy = i + 1;
            // c.fill = GridBagConstraints.BOTH;
            // c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            leavingValue.setHorizontalAlignment(SwingConstants.CENTER);
            leavingValue.setVerticalAlignment(SwingConstants.CENTER);
            leavingValue.setForeground(Color.WHITE);
            this.window.add(leavingValue, c);
        }

        // total progress bar
        JProgressBar totalBar = new JProgressBar();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 4;
        c.weightx = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 5, 5);
        // progress value
        totalBar.setValue(0);
        totalBar.setMaximum(config.roomCapacity);
        // progress string
        totalBar.setStringPainted(true);
        totalBar.setString("0");

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

        // get node components
        Component[] nodeComponents = ((Container) components[nodeLabelIndex]).getComponents();

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
