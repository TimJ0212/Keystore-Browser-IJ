package de.timjungk.keystorebrowser.logic;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.JComponentFileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DottedBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreEditor {
    private static final Logger LOG = Logger.getInstance(KeyStoreEditor.class);
    private static final String PASSWORD_TEXT = "Password:";
    private static final String LOAD_BUTTON_TEXT = "Load";
    private static final String WRONG_PASSWORD_MESSAGE = "The password you entered is incorrect. Please try again.";

    public static @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        JBPanel panel = createMainPanel();
        JScrollPane scrollPanel = new JBScrollPane();
        addPasswordComponents(panel, scrollPanel, file);
        return new JComponentFileEditor(file, createBorderLayoutPanel(scrollPanel, panel));
    }

    private static JBPanel createMainPanel() {
        return new JBPanel<>(new GridLayout(3, 1));
    }

    private static void addPasswordComponents(JBPanel panel, JScrollPane scrollPanel, VirtualFile file) {
        JBPasswordField passwordField = new JBPasswordField();
        JBLabel passwordLabel = new JBLabel(PASSWORD_TEXT);
        JButton button = createLoadButton(passwordField, scrollPanel, file);

        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(button);

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });
    }

    private static @NotNull JButton createLoadButton(JBPasswordField passwordField, JScrollPane scrollPanel, VirtualFile file) {
        JButton button = new JButton(LOAD_BUTTON_TEXT);
        button.addActionListener(actionEvent -> handleLoadAction(passwordField, scrollPanel, file));
        return button;
    }

    private static void handleLoadAction(JBPasswordField passwordField, JScrollPane scrollPanel, VirtualFile file) {
        if (passwordField.getPassword().length > 0) {
            try {
                scrollPanel.setViewportView(KeyStoreTableBuilder.createTable(file, passwordField.getPassword()));
                scrollPanel.revalidate();
                scrollPanel.repaint();
                passwordField.setBorder(BorderFactory.createLineBorder(JBColor.GRAY)); // Reset border to default
            } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
                LOG.warn("Failed to load keystore", e);
                showErrorDialog(passwordField, WRONG_PASSWORD_MESSAGE);
            }
        } else {
            showErrorDialog(passwordField, "Password cannot be empty.");
        }
    }

    private static void showErrorDialog(JBPasswordField passwordField, String message) {
        passwordField.setBorder(new DottedBorder(JBColor.RED));
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static JPanel createBorderLayoutPanel(JScrollPane scrollPanel, JBPanel panel) {
        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPanel, BorderLayout.CENTER);
        container.add(panel, BorderLayout.SOUTH);
        return container;
    }
}