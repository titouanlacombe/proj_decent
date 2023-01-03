package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import sim.Simulator;
import config.Config;

import java.awt.*;

public class MainWindow extends JFrame {

    JFrame window;
    Config config;
    Simulator sim;

    // main simulator window
    public MainWindow() {
        this(Config._default());
    }

    public MainWindow(Config config) {
        this.window = new JFrame("Simulateur");
        this.window.setSize(800, 600);
        this.config = config;

        // add window listener to handle window closing without exiting the program
        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {

                // ask for confirmation
                int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit the program?",
                        "Exit Program Message Box", JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {

                    // kill the nodes
                    if (sim != null) {
                        sim.killNodes();
                    }

                    // destroy the window
                    window.dispose();

                    System.exit(0);
                }
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
        System.out.println(config);

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
                        sim = new Simulator(config);
                        try {
                            sim.start();
                            clearWindow();
                            simulationWindow(config.numNodes);
                            sim.startSim();
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

    // public void updateSimulation() {

    // // get leaving and entering from Controllers inside sim's Room
    // int leaving = sim.getRoom().getLeaving();
    // int entering = sim.getRoom().getEntering();

    // }

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
