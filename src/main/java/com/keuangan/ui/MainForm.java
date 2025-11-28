package com.keuangan.ui;

import com.keuangan.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainForm extends JFrame {
    
    private User currentUser;
    
    public MainForm(User user) {
        this.currentUser = user;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Dashboard - Aplikasi Keuangan");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel utama
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Panel header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setLayout(null);
        
        JLabel lblWelcome = new JLabel("Selamat Datang, " + currentUser.getUsername() + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBounds(30, 25, 500, 30);
        headerPanel.add(lblWelcome);
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(680, 25, 90, 30);
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        headerPanel.add(btnLogout);
        
        // Panel konten
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Tombol-tombol menu
        JButton btnTransaksi = createMenuButton("Transaksi", "Kelola transaksi keuangan");
        JButton btnBudget = createMenuButton("Budget", "Atur budget bulanan");
        JButton btnStatistik = createMenuButton("Statistik", "Lihat statistik keuangan");
        JButton btnLaporan = createMenuButton("Laporan", "Generate laporan");
        
        btnBudget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BudgetForm().setVisible(true);
            }
        });

        contentPanel.add(btnTransaksi);
        contentPanel.add(btnBudget);
        contentPanel.add(btnStatistik);
        contentPanel.add(btnLaporan);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JButton createMenuButton(String title, String description) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + description + "</center></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(200, 100));
        button.setBackground(new Color(240, 240, 240));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        
        // Efek hover
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 220, 220));
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(240, 240, 240));
            }
        });
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainForm.this, 
                    "Fitur " + title + " akan segera tersedia!", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return button;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm().setVisible(true);
        }
    }
}

