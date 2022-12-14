package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import sim.Simulator;
import config.Config;

import java.awt.*;

public class MainWindow {

    Window window;
    Config config;

    // main simulator window
    public MainWindow() {
        this.window = new Window("Simulateur");
    }

    public MainWindow(Config config) {
        this.window = new Window("Simulateur");
        this.config = config;
    }

    public void startWindow() {
        // "Starting..."
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
        System.out.println("Config: " + config.toString());

        // Nodes field
        JTextField nodesTf = new JTextField();
        // label for nodesTf
        JLabel nodesLabel = new JLabel("Nombre de noeuds");
        // persons field
        JTextField personsTf = new JTextField();
        // label for personsTf
        JLabel personsLabel = new JLabel("Nombre de personnes");

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
                        Simulator simulator = new Simulator(config);
                        try {
                            simulator.start();
                            clearWindow();
                            simulationWindow(config.numNodes);
                            simulator.startSim();
                            simulator.killNodes();
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
        // 2nd column: progress bars for total and each node (80% of height)
        // margin of 5px on all sides

        GridBagLayout layout = new GridBagLayout();
        this.window.setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();

        // total label
        JLabel totalLabel = new JLabel("Total");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 5, 5);
        this.window.add(totalLabel, c);

        // node labels
        for (int i = 1; i <= nbNodes; i++) {
            JLabel nodeLabel = new JLabel("Node " + i);
            c.gridx = 0;
            c.gridy = i + 1;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 0.2;
            c.weighty = 0.2;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            this.window.add(nodeLabel, c);
        }

        // total progress bar
        JProgressBar totalBar = new JProgressBar();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.8;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 5, 5);
        this.window.add(totalBar, c);

        // node progress bars
        for (int i = 1; i <= nbNodes; i++) {
            JProgressBar nodeBar = new JProgressBar();
            c.gridx = 1;
            c.gridy = i + 1;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 0.8;
            c.weighty = 0.2;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(5, 5, 5, 5);
            this.window.add(nodeBar, c);
        }

        this.window.setVisible(true);

    }

    public void updateBar(int node, int value) {
        // update progress bar for node
        // update total progress bar

        // get all components
        Component[] components = this.window.getContentPane().getComponents();

        // total progress bar index
        int totalBarIndex = 1 + this.config.numNodes;

        // update total progress bar
        JProgressBar totalBar = (JProgressBar) components[totalBarIndex];
        totalBar.setValue(totalBar.getValue() + value);

        // update node progress bar
        JProgressBar nodeBar = (JProgressBar) components[totalBarIndex + node + 1];
        nodeBar.setValue(nodeBar.getValue() + value);

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

    public static void main(String[] args) {

        // create a new window
        MainWindow window = new MainWindow(Config._default());

        window.configWindow();
    }
}
