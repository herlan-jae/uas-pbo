/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Nama: Herlan Jaelani
 * NIM: 20230801327
 * Sesi: KJ101
 * Prodi: Teknik Informatika
 * Fakultas Ilmu Komputer
 * Mata Kuliah: Pemrograman Berorientasi Obyek (CSF308)
 * Dosen Pengampu: M. Bahrul Ulum, S.Kom, M.Kom
 * UJIAN AKHIR SEMESTER
 * 22 Januari 2025
*/

package uaspbo;
import java.sql.*;

public class DBConnect {
    private static final String DB = "jdbc:mysql://localhost:3306/shop";
    private static final String user = "root";
    private static final String password = "";
    
    public static Connection koneksiDatabase() {
     Connection connection = null;
     try {
         connection = DriverManager.getConnection(DB, user, password);
         System.out.println("Koneksi ke database berhasil");
     } catch (SQLException e) {
         System.out.println("Koneksi ke database gagal");
         e.printStackTrace();
     }
     return connection;
    }
}
