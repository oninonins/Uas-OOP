package com.keuangan.view;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.keuangan.view.pengeluaran.PengeluaranPanel;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Aplikasi Keuangan");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel pengeluaran
        mainPanel.add(new PengeluaranPanel(), "pengeluaran");

        add(mainPanel);

        showPengeluaranPanel();
    }

    public void showPengeluaranPanel() {
        cardLayout.show(mainPanel, "pengeluaran");
    }
}
