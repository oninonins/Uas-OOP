package com.keuangan.view.login;
import com.keuangan.util.UserSession;
import com.keuangan.model.User;
import com.keuangan.repository.UserRepo;
import com.keuangan.ui.MainForm;
import javax.swing.*;
import java.awt.*;
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister; 

    public LoginFrame() {
        setTitle("Login Aplikasi Keuangan");
        setSize(350, 200); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10)); 

        // Komponen GUI
        add(new JLabel("Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        // Baris Tombol Login
        btnLogin = new JButton("Login");
        add(new JLabel("")); // Spacer kosong
        add(btnLogin);

        // Baris Tombol Register (BARU)
        btnRegister = new JButton("Register Akun Baru");
        add(new JLabel("")); 
        add(btnRegister);

        // Event Klik
        btnLogin.addActionListener(e -> prosesLogin());
        btnRegister.addActionListener(e -> prosesRegister()); 
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        UserRepo repo = new UserRepo();
        User user = repo.login(username, password);

        if (user != null) {
            UserSession.setCurrentUser(user);
            JOptionPane.showMessageDialog(this, "Login Berhasil!");
            new MainForm(user).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 3. Logika Register
    private void prosesRegister() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // Validasi kosong
        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi username & password dulu!");
            return;
        }

        UserRepo repo = new UserRepo();

        // Cek kembar
        if(repo.cekUsername(username)) {
            JOptionPane.showMessageDialog(this, "Username sudah dipakai! Ganti yang lain.");
            return;
        }

        // Proses Simpan
        User userBaru = new User();
        userBaru.setUsername(username);
        userBaru.setPassword(password);

        if(repo.register(userBaru)) {
            JOptionPane.showMessageDialog(this, "Register Berhasil! Silakan Login.");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal Register. Coba lagi.");
        }
    }
}