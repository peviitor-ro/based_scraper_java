package com.peviitor.app;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class App {
    public static void main(String[] args) {
        // testing jsoup
        Document doc = Jsoup.parse("");
        try {
            doc = Jsoup.connect("https://en.wikipedia.org/").get();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        Element newsHeadlines = doc.select("#mp-itn b a").first();
        System.out.println(newsHeadlines.text());
    }
}
