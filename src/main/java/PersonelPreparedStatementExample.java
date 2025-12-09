package com.mustafasengul;

import java.sql.*;

public class PersonelPreparedStatementExample {
    public static void main(String[] args) {
        // Veritabanı bağlantı bilgileri
        String url = "jdbc:mysql://localhost:3307/personeldb";
        String kullaniciAdi = "root";
        String sifre = "12345";

        try {
            // 1. Veritabanına bağlan
            Connection baglanti = DriverManager.getConnection(url, kullaniciAdi, sifre);
            System.out.println("Veritabanına başarıyla bağlanıldı!");

           createTable(baglanti);

            // 2. Personel Ekleme (INSERT)
            String eklemeSorgusu = "INSERT INTO personel (ad, soyad, departman) VALUES (?, ?, ?)";
            try (PreparedStatement eklemeIfadesi = baglanti.prepareStatement(eklemeSorgusu)) {
                eklemeIfadesi.setString(1, "Mustafa");
                eklemeIfadesi.setString(2, "Şengül");
                eklemeIfadesi.setString(3, "Bilgi İşlem");
                eklemeIfadesi.executeUpdate();
                System.out.println("Yeni personel eklendi!");
            }

            // 3. Tüm Personelleri Listeleme (SELECT)
            listPersonel(baglanti);

            // 4. Personel Silme (DELETE)
            String silmeSorgusu = "DELETE FROM personel WHERE ad = ?";
            try (PreparedStatement silmeIfadesi = baglanti.prepareStatement(silmeSorgusu)) {
                silmeIfadesi.setString(1, "Mustafa");
                int silinenKayitSayisi = silmeIfadesi.executeUpdate();

                if (silinenKayitSayisi > 0) {
                    System.out.println("\nPersonel başarıyla silindi!");
                } else {
                    System.out.println("\nSilinecek personel bulunamadı!");
                }
            }

            // 5. Son durumu göster
            System.out.println("\n--- Son Durum ---");
            listPersonel(baglanti);

            // 6. Bağlantıyı kapat
            baglanti.close();

        } catch (SQLException e) {
            System.out.println("Veritabanı hatası oluştu: " + e.getMessage());
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS personel (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "ad VARCHAR(50) NOT NULL," +
                "soyad VARCHAR(50) NOT NULL," +
                "departman VARCHAR(50)" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Personel tablosu oluşturuldu (eğer yoksa)");
        }
    }

    private static void listPersonel(Connection conn) throws SQLException {
        String listelemeSorgusu = "SELECT * FROM personel";
        try (Statement stmt = conn.createStatement();
             ResultSet sonuclar = stmt.executeQuery(listelemeSorgusu)) {

            System.out.println("\n--- Tüm Personeller ---");
            System.out.println("ID | Ad      | Soyad   | Departman");
            System.out.println("-------------------------------");

            boolean kayitVar = false;
            while (sonuclar.next()) {
                kayitVar = true;
                int id = sonuclar.getInt("id");
                String ad = sonuclar.getString("ad");
                String soyad = sonuclar.getString("soyad");
                String departman = sonuclar.getString("departman");

                System.out.printf("%-2d | %-7s | %-7s | %s%n",
                        id, ad, soyad, departman);
            }

            if (!kayitVar) {
                System.out.println("Kayıt bulunamadı!");
            }
        }
    }
}
