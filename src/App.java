import ui.MainWindow;

import javax.swing.*;
import java.awt.Color;

import config.Config;
import sim.Simulator;

public class App {
	public static void main(String[] args) {
		Config config = Config._default();

		JFrame jframe = new JFrame(config.windowTitle);
		jframe.setSize(config.windowWidth, config.windowHeight);
		jframe.getContentPane().setBackground(Color.DARK_GRAY);

		Simulator sim = new Simulator(config);

		try {
			MainWindow mwindow = new MainWindow(jframe, config, sim);
			mwindow.initWindow();
			mwindow.configWindow();
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
}
