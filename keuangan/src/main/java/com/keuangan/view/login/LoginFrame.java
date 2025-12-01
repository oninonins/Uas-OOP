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

    public LoginFrame() {
        setTitle("Login Aplikasi Keuangan");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10)); 

        // Komponen GUI
        add(new JLabel("Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        btnLogin = new JButton("Login");
        add(new JLabel(""));
        add(btnLogin);

        btnLogin.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        UserRepo repo = new UserRepo();
        User user = repo.login(username, password);

        if (user != null) {
            // Login Sukses
            UserSession.setCurrentUser(user);
            JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat datang, " + user.getUsername());
            
            // TODO: Nanti di sini kita arahkan ke MainMenu
            new MainForm(user).setVisible(true);
            this.dispose();
            
        } else {
            
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

