/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package uaspbo;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author herla
 */
public class ShopJFrame extends javax.swing.JFrame {

    // Menyimpan Data Produk
    public void simpanData(
            String kodeProduk,
            String namaProduk,
            String satuan,
            int harga,
            int stok,
            String createDate) {
        String query = "INSERT INTO produk (KodeProduk, NamaProduk, Satuan, Harga, Stok, CreateDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.koneksiDatabase(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, kodeProduk);
            pstmt.setString(2, namaProduk);
            pstmt.setString(3, satuan);
            pstmt.setInt(4, harga);
            pstmt.setInt(5, stok);
            pstmt.setString(6, createDate);
            pstmt.executeUpdate();
            System.out.println("Data Produk berhasil disimpan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mengubah Data Produk
    private void ubahData(String kodeProduk, String namaProduk, String satuan, int harga, int stok) {
        String query = "UPDATE produk SET NamaProduk=?, Satuan=?, Harga=?, Stok=? WHERE KodeProduk=?";
        if (kodeProduk == null || kodeProduk.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode Produk tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (namaProduk == null || namaProduk.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Produk tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (harga <= 0) {
            JOptionPane.showMessageDialog(this, "Harga harus lebih besar dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (stok < 0) {
            JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DBConnect.koneksiDatabase(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pstmt.setString(1, namaProduk);
            pstmt.setString(2, satuan);
            pstmt.setInt(3, harga);
            pstmt.setInt(4, stok);
            pstmt.setString(5, kodeProduk);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan untuk diperbarui!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        tampilkanData();
    }

    // Menghapus Data Produk
    private void hapusData() {
        int selectedRow = ProdukTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String kodeProduk = ProdukTable.getValueAt(selectedRow, 0).toString();
        String query = "DELETE FROM produk WHERE KodeProduk=?";
        try (Connection conn = DBConnect.koneksiDatabase(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, kodeProduk);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            tampilkanData();
            resetForm();
            stateInaktif();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menampilkan Data di Tabel
    public void tampilkanData() {
        String query = "SELECT * FROM produk";
        try (Connection conn = DBConnect.koneksiDatabase(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = (DefaultTableModel) ProdukTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("KodeProduk"),
                    rs.getString("NamaProduk"),
                    rs.getString("Satuan"),
                    rs.getInt("Harga"),
                    rs.getInt("Stok"),
                    rs.getDate("CreateDate")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Memvalidasi Kode Produk
    public boolean validasiKode(String kodeProduk) {
        String query = "SELECT COUNT(*) FROM produk WHERE KodeProduk = ?";
        try (Connection conn = DBConnect.koneksiDatabase(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, kodeProduk);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Memvalidasi Input Data Produk
    private boolean validasiInput() {
        if (KodeProdukTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode Produk wajib diisi!", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
            KodeProdukTextField.requestFocus();
            return false;
        }
        if (NamaProdukTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Produk wajib diisi!", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
            NamaProdukTextField.requestFocus();
            return false;
        }
        if (SatuanComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih satuan produk!", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
            SatuanComboBox.requestFocus();
            return false;
        }
        if (HargaTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga Produk wajib diisi!", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
            HargaTextField.requestFocus();
            return false;
        }
        if (StokTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stok Produk wajib diisi!", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
            StokTextField.requestFocus();
            return false;
        }

        try {
            Integer.parseInt(HargaTextField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
            HargaTextField.requestFocus();
            return false;
        }

        try {
            Integer.parseInt(StokTextField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok harus berupa angka!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            StokTextField.requestFocus();
            return false;
        }

        return true;
    }

    // State Komponen Aktif
    private void stateAktif() {
        KodeProdukTextField.setEnabled(true);
        NamaProdukTextField.setEnabled(true);
        SatuanComboBox.setEnabled(true);
        HargaTextField.setEnabled(true);
        StokTextField.setEnabled(true);
        SaveButton.setEnabled(true);
        CancelButton.setEnabled(true);
    }

    // State Komponen Inaktif
    private void stateInaktif() {
        KodeProdukTextField.setEnabled(false);
        NamaProdukTextField.setEnabled(false);
        SatuanComboBox.setEnabled(false);
        HargaTextField.setEnabled(false);
        StokTextField.setEnabled(false);
        SaveButton.setEnabled(false);
        UpdateButton.setEnabled(false);
        DeleteButton.setEnabled(false);
        CancelButton.setEnabled(false);
        AddNewButton.setEnabled(true);
        PrintButton.setEnabled(true);
    }

    // State Button
    private void buttonState(
            boolean addNew,
            boolean print,
            boolean save,
            boolean update,
            boolean delete,
            boolean cancel
    ) {
        AddNewButton.setEnabled(addNew);
        PrintButton.setEnabled(print);
        SaveButton.setEnabled(save);
        UpdateButton.setEnabled(update);
        DeleteButton.setEnabled(delete);
        CancelButton.setEnabled(cancel);
    }

    // State awal TextField
    private void fieldState(boolean enable) {
        KodeProdukTextField.setEnabled(enable);
        NamaProdukTextField.setEnabled(enable);
        SatuanComboBox.setEnabled(enable);
        HargaTextField.setEnabled(enable);
        StokTextField.setEnabled(enable);
    }

    // Reset TextField
    private void resetForm() {
        KodeProdukTextField.setText("");
        NamaProdukTextField.setText("");
        SatuanComboBox.setSelectedIndex(0);
        HargaTextField.setText("");
        StokTextField.setText("");
    }

    public ShopJFrame() {
        initComponents();

        tampilkanData();

        // Trigger Tombol ADD NEW
        AddNewButton.addActionListener(e -> {
            stateAktif();
            resetForm();
            KodeProdukTextField.requestFocus();
        });

        // Trigger Tombol SAVE
        SaveButton.addActionListener(e -> {
            // Validasi semua TextField
            if (KodeProdukTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode Produk tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                KodeProdukTextField.requestFocus();
                return;
            }
            if (NamaProdukTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama Produk tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                NamaProdukTextField.requestFocus();
                return;
            }
            if (HargaTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harga tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                HargaTextField.requestFocus();
                return;
            }
            if (StokTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                StokTextField.requestFocus();
                return;
            }

            String kodeProduk = KodeProdukTextField.getText();
            String namaProduk = NamaProdukTextField.getText();
            String satuan = (String) SatuanComboBox.getSelectedItem();
            int harga = Integer.parseInt(HargaTextField.getText());
            int stok = Integer.parseInt(StokTextField.getText());

            if (ProdukTable.getSelectedRow() != -1) {
                ubahData(kodeProduk, namaProduk, satuan, harga, stok);
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                simpanData(kodeProduk, namaProduk, satuan, harga, stok, new java.sql.Date(System.currentTimeMillis()).toString());
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }

            tampilkanData();
            resetForm();
        });

        // Trigger Tombol DELETE
        DeleteButton.addActionListener(e -> {
            hapusData();
        });

        // Trigger Tombol CANCEL
        CancelButton.addActionListener(e -> {
            resetForm();
            ProdukTable.clearSelection();
            fieldState(false);
            buttonState(true, true, false, false, false, false);
        });

        // Tombol Update
        UpdateButton.addActionListener(e -> {
            int selectedRow = ProdukTable.getSelectedRow();
            if (selectedRow != -1) {
                String kodeProduk = ProdukTable.getValueAt(selectedRow, 0).toString();
                String namaProduk = ProdukTable.getValueAt(selectedRow, 1).toString();
                String satuan = ProdukTable.getValueAt(selectedRow, 2).toString();
                String harga = ProdukTable.getValueAt(selectedRow, 3).toString();
                String stok = ProdukTable.getValueAt(selectedRow, 4).toString();

                KodeProdukTextField.setText(kodeProduk);
                NamaProdukTextField.setText(namaProduk);
                SatuanComboBox.setSelectedItem(satuan);
                HargaTextField.setText(harga);
                StokTextField.setText(stok);

                fieldState(true);
                buttonState(false, true, true, false, false, true);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan diupdate!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Listener Tabel Produk
        ProdukTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && ProdukTable.getSelectedRow() != -1) {
                int selectedRow = ProdukTable.getSelectedRow();
                KodeProdukTextField.setText(ProdukTable.getValueAt(selectedRow, 0).toString());
                NamaProdukTextField.setText(ProdukTable.getValueAt(selectedRow, 1).toString());
                SatuanComboBox.setSelectedItem(ProdukTable.getValueAt(selectedRow, 2).toString());
                HargaTextField.setText(ProdukTable.getValueAt(selectedRow, 3).toString());
                StokTextField.setText(ProdukTable.getValueAt(selectedRow, 4).toString());
                buttonState(false, true, false, true, true, true);
            }
        });

        // Item Satuan
        SatuanComboBox.removeAllItems();
        SatuanComboBox.addItem("Pilih");
        SatuanComboBox.addItem("Pcs");
        SatuanComboBox.addItem("Box");
        SatuanComboBox.addItem("Kg");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        KodeProdukTextField = new javax.swing.JTextField();
        NamaProdukTextField = new javax.swing.JTextField();
        HargaTextField = new javax.swing.JTextField();
        SatuanComboBox = new javax.swing.JComboBox<>();
        StokTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ProdukTable = new javax.swing.JTable();
        AddNewButton = new javax.swing.JButton();
        PrintButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();
        UpdateButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 0, 153));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("JetBrains Mono", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DATA PRODUK");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(408, 408, 408))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        SatuanComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("JetBrains Mono", 1, 12)); // NOI18N
        jLabel2.setText("Kode Produk");

        jLabel3.setFont(new java.awt.Font("JetBrains Mono", 1, 12)); // NOI18N
        jLabel3.setText("Nama Produk");

        jLabel4.setFont(new java.awt.Font("JetBrains Mono", 1, 12)); // NOI18N
        jLabel4.setText("Satuan");

        jLabel5.setFont(new java.awt.Font("JetBrains Mono", 1, 12)); // NOI18N
        jLabel5.setText("Harga");

        jLabel6.setFont(new java.awt.Font("JetBrains Mono", 1, 12)); // NOI18N
        jLabel6.setText("Stok");

        ProdukTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Kode Barang", "Nama Barang", "Satuan", "Harga", "Stok", "Create Date"
            }
        ));
        jScrollPane1.setViewportView(ProdukTable);

        AddNewButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        AddNewButton.setText("Add New");

        PrintButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        PrintButton.setText("Print");

        SaveButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        SaveButton.setText("Save");

        UpdateButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        UpdateButton.setText("Update");

        DeleteButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        DeleteButton.setText("Delete");

        CancelButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        CancelButton.setText("Cancel");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(KodeProdukTextField)
                            .addComponent(NamaProdukTextField)
                            .addComponent(HargaTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SatuanComboBox, 0, 267, Short.MAX_VALUE)
                            .addComponent(StokTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(AddNewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PrintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 147, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(KodeProdukTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(NamaProdukTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SatuanComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(HargaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(StokTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddNewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PrintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ShopJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ShopJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ShopJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShopJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ShopJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNewButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JTextField HargaTextField;
    private javax.swing.JTextField KodeProdukTextField;
    private javax.swing.JTextField NamaProdukTextField;
    private javax.swing.JButton PrintButton;
    private javax.swing.JTable ProdukTable;
    private javax.swing.JComboBox<String> SatuanComboBox;
    private javax.swing.JButton SaveButton;
    private javax.swing.JTextField StokTextField;
    private javax.swing.JButton UpdateButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
