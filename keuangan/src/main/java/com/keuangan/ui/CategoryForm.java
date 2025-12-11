package com.keuangan.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.keuangan.dao.CategoryDAO;
import com.keuangan.model.Category;
import com.keuangan.model.User;

public class CategoryForm extends JDialog {

    private final User currentUser;
    private final CategoryDAO categoryDAO;
    private final Runnable onSuccess;

    private JTextField txtName;

    public CategoryForm(Frame owner, User user, Runnable onSuccess) {
        super(owner, "Tambah Kategori", true);
        this.currentUser = user;
        this.categoryDAO = new CategoryDAO();
        this.onSuccess = onSuccess;

        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formPanel.add(new JLabel("Nama Kategori:"));
        txtName = new JTextField(20);
        formPanel.add(txtName);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");
        btnSave.addActionListener(this::handleSave);
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSave(ActionEvent e) {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori wajib diisi.");
            return;
        }

        Category category = new Category(currentUser.getId(), name);
        boolean success = categoryDAO.insert(category);
        if (success) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil ditambahkan.");
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kategori (mungkin sudah ada).");
        }
    }
}

