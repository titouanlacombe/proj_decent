package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.*;

public class MainWindow {

    Window window;
    int numNodes;
    int numPersons;

    // main simulator window
    public MainWindow() {
        this.window = new Window("Simulateur");
    }

    public MainWindow(int numNodes, int numPersons) {
        this.window = new Window("Simulateur");
        this.numNodes = numNodes;
        this.numPersons = numPersons;
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
        nodesTf.setText(Integer.toString(numNodes));

        personsLabel.setBounds(50, 60, 200, 30);
        personsTf.setBounds(220, 60, 100, 30);
        personsTf.setText(Integer.toString(numPersons));

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

                        numNodes = Integer.parseInt(nodesTf.getText());
                        numPersons = Integer.parseInt(personsTf.getText());
                        // Config config = new Config(nb_nodes, nb_persons);
                        clearWindow();

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
        // 1st row: labels for total and each node (20% of height)
        // 2nd row: vertical progress bars for total and each node (80% of height)
        // margin of 5px on all sides

        GridBagLayout layout = new GridBagLayout();
        this.window.setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.2;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);

        // 1st row
        // center label text
        JLabel totalLabel = new JLabel("Total");
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layout.setConstraints(totalLabel, c);
        this.window.add(totalLabel);

        c.gridx = 1;
        for (int i = 0; i < nbNodes; i++) {
            JLabel nodeLabel = new JLabel("Noeud " + i);
            nodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            layout.setConstraints(nodeLabel, c);
            this.window.add(nodeLabel);
            c.gridx++;
        }

        // 2nd row
        c.weighty = 0.8;
        c.gridx = 0;
        c.gridy = 1;

        JProgressBar totalBar = new JProgressBar(JProgressBar.VERTICAL);
        layout.setConstraints(totalBar, c);
        this.window.add(totalBar);

        c.gridx = 1;
        int total = 0;
        for (int i = 0; i < nbNodes; i++) {
            // Vertical progress bar
            JProgressBar nodeBar = new JProgressBar(JProgressBar.VERTICAL);
            nodeBar.setValue(0);
            nodeBar.setStringPainted(true);
            layout.setConstraints(nodeBar, c);
            this.window.add(nodeBar);
            c.gridx++;

            // update total
            total += nodeBar.getValue();
        }
        totalBar.setValue(total);
        totalBar.setStringPainted(true);

        this.window.setVisible(true);

    }

    public void updateBar(int node, int value) {
        // update progress bar for node
        // update total progress bar

        // get all components
        Component[] components = this.window.getContentPane().getComponents();

        // index of the total progress bar
        int totalBarIndex = 1 + numNodes;

        // get total progress bar
        JProgressBar totalBar = (JProgressBar) components[totalBarIndex];

        // get node progress bar
        JProgressBar nodeBar = (JProgressBar) components[totalBarIndex + node + 1];

        // update node progress bar
        nodeBar.setValue(value);

        // update total progress bar
        int total = 0;
        for (int i = totalBarIndex; i < components.length; i++) {
            // if not total bar
            if (i != totalBarIndex)
                total += ((JProgressBar) components[i]).getValue();

        }
        totalBar.setValue(total);

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
