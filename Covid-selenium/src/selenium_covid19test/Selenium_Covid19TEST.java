package selenium_covid19test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium_Covid19TEST extends Thread{
    File makaleler = new File("makaleler.txt");  
    public static void makaleTopla() throws IOException, InterruptedException{        
        File makaleler = new File("makaleler.txt");
        
        if (!makaleler.exists()) {                                                                   ////makaleler isimli bir dosya yoksa oluşturuyor.
            makaleler.createNewFile();
        }
        FileWriter makaleWriter = new FileWriter(makaleler, false);
        BufferedWriter bMakale = new BufferedWriter(makaleWriter);
        
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY,"true");        //Chrome üzerinde gezinti yapıldığı için "chromedriver.exe" isimli otomatik çalışan
        System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");                                                               //bir eklenti kullanılması gerekir. Dosya yolu önemlidir!!.
        WebDriver browser = new ChromeDriver();                                                     //Yeni bir tarayıcı açar.
        WebDriverWait wait = new WebDriverWait(browser, 30);                                        //Sitelerin yüklenmesi için 30sn boyutunda bir tolerans süresi verir.
        
        String baseUrl = "https://www.ncbi.nlm.nih.gov/pubmed/?term=covid+19";                      //Bağlanılan sitenin url adresi.
        browser.get(baseUrl);                                                                       //Url adresini tarayıcıya yükler.
        
        WebElement sortByMenu=browser.findElement(By.xpath("/html/body/div[2]/div[1]/form/div[1]/div[7]/div/div[1]/ul/li[2]/a")); 
        sortByMenu.click();                                                                                                             //Sitedeki en çok tıklanan makaleler için.
        WebElement bestMatch = wait.until(ExpectedConditions.elementToBeClickable(By.id("[relevance]")));
        bestMatch.click();
        browser.navigate().refresh();
        WebElement perPage=wait.until(ExpectedConditions.elementToBeClickable(browser.findElement(By.xpath("/html/body/div[2]/div[1]/form/div[1]/div[7]/div/div[1]/ul/li[3]/a"))));
        perPage.click();                                                                                                                //Sayfada 100 makale birden görüntülemek için.
        WebElement hundred = wait.until(ExpectedConditions.elementToBeClickable(By.id("ps100")));
        hundred.click();
        
        String link="",dokuman="";
        int pageLenght=100,n=0;
        
        for(int i =0; i<pageLenght; i++){
            List<WebElement> anchors = browser.findElements(By.className("rslt"));
            try{
                n++;
                System.out.println(n);
                link=anchors.get(i).findElement(By.tagName("a")).getAttribute("href");
                System.out.println("++++++ "+link);
                browser.navigate().to(link);                                                                                //Sayfaları tek tek gezer ve içindeki makaleleri alır.
                dokuman=browser.findElement(By.className("abstr")).getText();
                bMakale.newLine();
                bMakale.write("--  Makale "+n+" --");                                                                       // Daha sonra işlenmek üzere makaleler.txt dosyasına kaydeder.
                bMakale.write(dokuman);
                System.out.println(dokuman);
                browser.navigate().back(); 
            }
            catch(Exception e) { 
                browser.navigate().back(); 
                System.out.println(e);  
            }                
        }
        bMakale.close();
        browser.close();
    }
    public static void genCikar() throws IOException{
        List<String> genList = new ArrayList<String>();
        HashMap<Object,Object> GenTekrarHmap = new HashMap<Object,Object>();
        HashMap<Object,Object> OranHmap = new HashMap<Object,Object>();
        String gen="";
        int CovidCount=0;
        File makaleler = new File("makaleler.txt");
        File genler = new File("genler.txt");
        if (!genler.exists() && !makaleler.exists()) {
            genler.createNewFile();                                                                                        // genler.txt dosyası oluşturulur ve makaleler getirilir.
            makaleler.createNewFile();
        }
        Scanner sc = new Scanner(makaleler);
        FileWriter genWriter = new FileWriter(genler, false);
        BufferedWriter bGen = new BufferedWriter(genWriter);
        while(sc.hasNext()){
            gen=sc.next();                                                                                   //makaleler okunur ve içerisindeki genler çıkarılır ve temizlendikten sonra
            if (Pattern.matches(".*[A-Z][A-Z].*",gen)){                                                                                     //genler.txt dosyasına kaydedilir.
                gen = gen.replaceAll("[^a-zA-Z0-9]", "");
                if(!genList.contains(gen) && gen.length()<6){
                    genList.add(gen);
                    bGen.write(gen);
                    bGen.newLine();
                    for(String genS : genList) {
                        if(!GenTekrarHmap.containsKey(genS)){                                               // burada kayıtlı olan genler sayılır.
                            GenTekrarHmap.put(genS,1);
                        }else{
                            int count=(int)GenTekrarHmap.get(genS);
                            GenTekrarHmap.put(genS,count+1);
                        }   
                    }
                }
            }
            if(gen.contains("CoV")){                                                                           //CoV-19 gen sayısı
                CovidCount++;
            }
        }
        for(Map.Entry<Object, Object> entry : GenTekrarHmap.entrySet()) {
            Object key = entry.getKey();
            double value = ((Integer)entry.getValue()).intValue();                                              //oranların hesaplanıp kaydedildiği Hmap yapısı
            double rate=value/CovidCount;
            OranHmap.put(key,rate);
        }
        
        System.out.println("\nGen Oranları: "+OranHmap);
        
        bGen.close();
        sc.close();
    }
    public static void main(String[] args) throws InterruptedException, IOException {
    	makaleTopla();
        genCikar();
    }
}