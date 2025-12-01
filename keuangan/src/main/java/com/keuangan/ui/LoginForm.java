package com.keuangan.ui;

import com.keuangan.dao.UserDAO;
import com.keuangan.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private UserDAO userDAO;
    
    public LoginForm() {
        userDAO = new UserDAO();
        initComponents();
    }
    
    private void initComponents() {
        // Setting frame
        setTitle("Login - Aplikasi Keuangan");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel utama
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Label judul
        JLabel lblTitle = new JLabel("APLIKASI KEUANGAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(100, 30, 250, 30);
        mainPanel.add(lblTitle);
        
        // Label username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 90, 100, 25);
        mainPanel.add(lblUsername);
        
        // Text field username
        txtUsername = new JTextField();
        txtUsername.setBounds(150, 90, 180, 25);
        mainPanel.add(txtUsername);
        
        // Label password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 130, 100, 25);
        mainPanel.add(lblPassword);
        
        // Password field
        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 130, 180, 25);
        mainPanel.add(txtPassword);
        
        // Tombol Login
        btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 180, 80, 30);
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.BLACK); // Ubah warna teks menjadi hitam
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                prosesLogin();
            }
        });
        mainPanel.add(btnLogin);
        
        // Tombol Register
        btnRegister = new JButton("Register");
        btnRegister.setBounds(250, 180, 80, 30);
        btnRegister.setBackground(new Color(60, 179, 113));
        btnRegister.setForeground(Color.BLACK); // Ubah warna teks menjadi hitam
        btnRegister.setFocusPainted(false);
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                prosesRegister();
            }
        });
        mainPanel.add(btnRegister);
        
        // Tambahkan panel ke frame
        add(mainPanel);
        
        // Biar bisa tekan Enter untuk login
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    prosesLogin();
                }
            }
        });
    }
    
    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username dan Password harus diisi!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cek ke database
        User user = userDAO.login(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Login berhasil! Selamat datang, " + user.getUsername(), 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Tutup form login dan buka halaman utama
            this.dispose();
            new MainForm(user).setVisible(true);
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "Username atau Password salah!", 
                "Login Gagal", 
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
    
    private void prosesRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username dan Password harus diisi!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Username minimal 3 karakter!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "Password minimal 4 karakter!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cek apakah username sudah ada
        if (userDAO.isUsernameExist(username)) {
            JOptionPane.showMessageDialog(this, 
                "Username sudah digunakan! Pilih username lain.", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Proses register
        boolean berhasil = userDAO.register(username, password);
        
        if (berhasil) {
            JOptionPane.showMessageDialog(this, 
                "Registrasi berhasil! Silakan login.", 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registrasi gagal! Coba lagi.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Menggunakan Look and Feel sesuai sistem operasi
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Jalankan form login
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}

