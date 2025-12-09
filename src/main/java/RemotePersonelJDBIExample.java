package com.mustafasengul;

import org.jdbi.v3.core.Jdbi;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RemotePersonelJDBIExample {
    public static void main(String[] args) {
        try {
            //MySQL Driver'ı yükle
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Aiven MySQL bağlantı bilgileri
            String url = "jdbc:mysql://mysql-3bce58c7-mustafasengul29-1a8c.g.aivencloud.com:24776/defaultdb?useSSL=true&verifyServerCertificate=false";
            String user = "avnadmin";
            String password = "AVNS_XBV8mu7aYFrc-MvCAwn";

            //Bağlantıyı test et
            try (java.sql.Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("Bağlantı başarılı! (Connection successful!)");
            }

            //JDBI nesnesi oluştur
            Jdbi jdbi = Jdbi.create(url, user, password);

            //Tabloyu oluştur (eğer yoksa)
            jdbi.useHandle(handle -> {
                handle.execute("CREATE TABLE IF NOT EXISTS ad_soyad (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "isim VARCHAR(50)," +
                        "soyisim VARCHAR(50)," +
                        "maas DECIMAL(10,2))");
                System.out.println("Uzak personel tablosu oluşturuldu (eğer yoksa)");
            });

            //3 personel ekle
            jdbi.useHandle(handle -> {
                handle.createUpdate("INSERT INTO ad_soyad (isim, soyisim, maas) VALUES (:isim, :soyisim, :maas)")
                        .bind("isim", "Mustafa")
                        .bind("soyisim", "Şengül")
                        .bind("maas", 100000)
                        .execute();

                handle.createUpdate("INSERT INTO ad_soyad (isim, soyisim, maas) VALUES (:isim, :soyisim, :maas)")
                        .bind("isim", "Hayrullah")
                        .bind("soyisim", "Öztürk")
                        .bind("maas", 150000)
                        .execute();

                handle.createUpdate("INSERT INTO ad_soyad (isim, soyisim, maas) VALUES (:isim, :soyisim, :maas)")
                        .bind("isim", "Deniz")
                        .bind("soyisim", "Yazıcı")
                        .bind("maas", 120000)
                        .execute();

                System.out.println("3 personel eklendi!");
            });

            //Tüm verileri listele
            jdbi.useHandle(handle -> {
                System.out.println("\n--- Tüm Personeller ---");
                handle.createQuery("SELECT * FROM ad_soyad")
                        .map((rs, ctx) -> rs.getInt("id") + " | " +
                                rs.getString("isim") + " | " +
                                rs.getString("soyisim") + " | " +
                                rs.getDouble("maas"))
                        .list()
                        .forEach(System.out::println);
            });

            //Bir kaydı sil (Hayrullah)
            jdbi.useHandle(handle -> {
                handle.createUpdate("DELETE FROM ad_soyad WHERE isim = :isim")
                        .bind("isim", "Hayrullah")
                        .execute();
                System.out.println("\nHayrullah başarıyla silindi!");
            });

            //Son durumu göster
            jdbi.useHandle(handle -> {
                System.out.println("\n--- Son Durum ---");
                handle.createQuery("SELECT * FROM ad_soyad")
                        .map((rs, ctx) -> rs.getInt("id") + " | " +
                                rs.getString("isim") + " | " +
                                rs.getString("soyisim") + " | " +
                                rs.getDouble("maas"))
                        .list()
                        .forEach(System.out::println);
            });

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver bulunamadı! pom.xml dosyasını kontrol edin.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası (Database connection error):");
            e.printStackTrace();
        }
    }
}
