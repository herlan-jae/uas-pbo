/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
