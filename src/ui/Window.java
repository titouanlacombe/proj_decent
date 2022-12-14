package ui;

import javax.swing.*;

class Window extends JFrame {
    private static final long serialVersionUID = 1L;

    public Window(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setVisible(true);
    }

    public Window(String title) {
        this(title, 800, 600);
    }
}