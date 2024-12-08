package de.timjungk.keystorebrowser.logic;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.table.JBTable;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class KeyStoreTableBuilder {


    public static JBTable createTable(final VirtualFile file, final char[] password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(new File(file.getPath()), password);
        String[] columnNames = {"Alias", "Type", "Algorithm", "Key Size", "Details", "Issuer", "Subject", "Valid From", "Expiration Date", "Serial Number"};

        String[][] data = new String[ks.size()][columnNames.length];
        int index = 0;
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            final Certificate cert = ks.getCertificate(alias);
            if (cert instanceof X509Certificate x509Cert) {
                String issuer = x509Cert.getIssuerX500Principal().getName();
                String subject = x509Cert.getSubjectX500Principal().getName();
                String validFrom = x509Cert.getNotBefore().toString();
                String expiryDate = x509Cert.getNotAfter().toString();
                String serialNumber = x509Cert.getSerialNumber().toString();
                String algorithm = cert.getPublicKey().getAlgorithm();
                int keySize = cert.getPublicKey().getEncoded().length * 8;


                data[index] = new String[]{alias, cert.getType(), algorithm, String.valueOf(keySize), x509Cert.toString(), issuer, subject, validFrom, expiryDate, serialNumber};
            } else {

                data[index] = new String[]{alias, cert.getType(), "N/A", "N/A", cert.toString(), "N/A", "N/A", "N/A", "N/A", "N/A"};
            }
            index++;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JBTable table = new JBTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                if (row >= 0 && column >= 0) {
                    Object value = table.getValueAt(row, column);
                    if (value != null) {
                        showDetailDialog(value.toString());
                    }
                }
            }
        });

        return table;

    }

    private static void showDetailDialog(String content) {
        JBTextArea textArea = new JBTextArea(content);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        JBScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        DialogBuilder dialogBuilder = new DialogBuilder();
        dialogBuilder.centerPanel(scrollPane);
        dialogBuilder.setTitle("Cell Details");
        dialogBuilder.show();
    }


}
