package com.keuangan;

import javax.swing.SwingUtilities;

import com.keuangan.view.MainFrame;

public class App {
    public static void main(String[] args) {
        // Pastikan UI dibuat di thread event-dispatching Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true); // tampilkan JFrame
        });
    }
}
