import ui.MainWindow;
import utils.Logger;
import utils.Logging;

import javax.swing.*;
import java.awt.Color;

import config.Config;
import sim.Simulator;

public class App {
    public void _main(String[] args) throws Exception {
        Logging.init("App", "./data/app.log");

        Config config = Config._default();

        JFrame jframe = new JFrame(config.windowTitle);
        jframe.setSize(config.windowWidth, config.windowHeight);
        jframe.getContentPane().setBackground(Color.DARK_GRAY);

        MainWindow mwindow = new MainWindow(Logger.fileLogger("UI", "./data/ui.log"), jframe, config);
        mwindow.initWindow();
        mwindow.configWindow();
    }

    public static void main(String[] args) {
        App app = new App();

        try {
            app._main(args);
        } catch (Exception e) {
            Logging.exception(e);
        }
    }
}
