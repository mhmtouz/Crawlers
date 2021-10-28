package webcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

    static String sonuc = "";
    static String yaz = "";
    static int sayac;
    static int bosluksayac = 0;
    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;

    public static void DosyaYaz(String FilePath, String metin) {
        try {
            File dosya = new File(FilePath);
            FileWriter yazici = new FileWriter(dosya, true);
            BufferedWriter yaz = new BufferedWriter(yazici);
            yaz.write(metin);
            yaz.close();
            System.out.println("Ekleme İşlemi Başarılı");
        } catch (Exception hata) {
            hata.printStackTrace();
        }
    }

    public static void veritabani_baglan() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/saglik?useUnicode=true&characterEncoding=UTF-8";
        String kullaniciad = "root";
        String sifre = "";
        con = DriverManager.getConnection(url, kullaniciad, sifre);
        st = con.createStatement();
        System.out.println("VERİ TABANINA BAGLANDI");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        veritabani_baglan();
        String harfler[] = {"A", "B", "C", "Ç", "D", "E", "F", "G", "H", "İ", "K", "L", "M", "O", "Ö", "P", "R", "S", "Ş", "T", "U", "Ü", "V", "Y", "Z"};
        for (int j = 0; j < harfler.length; j++) {
            System.out.println(harfler[j] + "------------ÇEKİLİYOR");
            Document doc = Jsoup.connect("https://sagliklokman.com/tr/sozluk").get();
            Elements links = doc.getElementById(harfler[j]).select("a");
            for (int i = 0; i < links.size(); i++) {
                String veri = links.get(i).attr("href");
                mail("https://sagliklokman.com/" + veri);
            }
        }
    }

    public static void mail(String a) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        bosluksayac = 0;
        String sutun = "";
        String b = "";
        int code = 0;
        HttpURLConnection connection = null;
        try {
            URL siteURL = new URL(a);
            connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Document doc1 = Jsoup.connect(a).get();
                Elements links1 = doc1.getElementsByClass("col-md-12");
                String teshisler = links1.select("p").get(0).text();
                teshisler = teshisler.replace("'", "");
                teshisler = teshisler.substring(teshisler.indexOf("("), teshisler.indexOf(")"));
                teshisler = teshisler.replace("(", "");
                System.err.println("teshisler = " + teshisler);
                String verigonder = "INSERT INTO lokman (teshis) VALUES ('" + teshisler + "')";
                st.execute(verigonder);

                Elements links2 = doc1.getElementsByClass("col-md-12").select("p").select("a");
                for (int j = 0; j < links2.size(); j++) {
                    b = links2.get(j).attr("href");
                    try {
                        bosluksayac++;
                        Document doc2 = Jsoup.connect("https://sagliklokman.com" + b).get();
                        Elements links3 = doc2.getElementsByClass("main-top bg-blue").select(".row");
                        String hastalik = links3.select("p").get(0).text();
                        hastalik = hastalik.substring(0, hastalik.indexOf("için") - 1);
                        sutun = sutun + hastalik + " ";
                        System.out.println(hastalik + "\t" + sutun);
                    } catch (Exception e) {
                        bosluksayac--;
                    }
                }
                sutun = sutun.substring(0, sutun.length() - 1);
                String sorgu = "UPDATE lokman SET semptomlar='" + sutun + "' WHERE teshis='" + teshisler + "'";
                st.executeUpdate(sorgu);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void metin(String m, String baslik) {
        sayac++;
        String deger = "";
        String sil;

        try {
            Document doc2 = Jsoup.connect(m).get();
            Elements links2 = doc2.getElementsByClass("makale").select("p");
            for (int j = 0; j < links2.size(); j++) {

                String son = links2.get(j).text();
                deger = deger + son + "\n";
            }
            baslik = baslik.replace("?", "");
            try {
                File dosya = new File("D:\\makaleler\\" + baslik + ".txt");
                FileWriter yazici = new FileWriter(dosya);
                BufferedWriter yaz = new BufferedWriter(yazici);
                yaz.write(deger);
                yaz.close();
                System.out.println("Yazma İşlemi Başarılı");
            } catch (Exception hata) {
                hata.printStackTrace();
            }

        } catch (Exception e) {
        }
    }
}
