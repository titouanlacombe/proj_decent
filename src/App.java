import ui.MainWindow;

import javax.swing.*;

import config.Config;
import sim.Simulator;

public class App {
    public static void main(String[] args) {
        Config config = Config._default();

        JFrame jframe = new JFrame(config.windowTitle);
        jframe.setSize(config.windowWidth, config.windowHeight);

        Simulator sim = new Simulator(config);

        MainWindow mwindow = new MainWindow(jframe, config, sim);
        mwindow.initWindow();
        mwindow.configWindow();
    }
}
