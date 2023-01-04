package ui;

public class SimProgBar extends javax.swing.JProgressBar {

    public SimProgBar(int max) {
        super();

        // progress value
        this.setValue(0);
        this.setMaximum(max);
        // progress string
        this.setStringPainted(true);
        this.setString("0");
    }

}
