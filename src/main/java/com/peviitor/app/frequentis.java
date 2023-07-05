package com.peviitor.app;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class frequentis {
    public static JSONObject company = new JSONObject().put("company", "Frequentis");
    public static String country = "Romania";
    public static void main(String[] args) {
        Document document = Jsoup.parse("");
        try {
            document = Jsoup.connect("https://jobs.frequentis.com/careers/SearchJobs/?1302=%5B858865%5D&1302_format=775&listFilterMode=1").get();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();
        
        Elements jobElements = document.getElementsByClass("list--jobs").first().getElementsByClass("list__item__text");
        for (Element element : jobElements) {
            JSONObject job = new JSONObject();

            job.put("id", UUID.randomUUID().toString());
            job.put("job_title", element.getElementsByClass("list__item__text__title").text());
            job.put("job_link", element.getElementsByTag("a")
                                            .first()
                                            .attr("href"));
            job.put("company", company.getString("company"));
            job.put("city", element.getElementsByClass("list__item__text__subtitle")
                                            .text()
                                            .split(" ")[2]);
            job.put("country", country);

            jobs.add(job);
        }

        System.out.println(JSONObject.valueToString(jobs));

        try {
            String apikey = System.getenv("APIKEY");
            // // Clean Data from API
            utils.post("https://api.peviitor.ro/v4/clean/", JSONObject.valueToString(company), new JSONObject[] {
                new JSONObject().put("Content-Type", "application/x-www-form-urlencoded")
                .put("apikey", apikey)
            });

            // // Add Data to API
            utils.post("https://api.peviitor.ro/v4/update/", JSONObject.valueToString(jobs), new JSONObject[] {
                new JSONObject().put("Content-Type", "application/json")
                .put("apikey", apikey)
            });

            // Add logo
            String logo = "https://www.frequentis.com/themes/custom/frequentis/images/social/fb_default.jpg";
            ArrayList<JSONObject> logoData = new ArrayList<JSONObject>();
            logoData.add(new JSONObject().put("id", company.getString("company")).put("logo", logo));

            utils.post("https://api.peviitor.ro/v1/logo/add/", JSONObject.valueToString(logoData), new JSONObject[] {
                new JSONObject().put("Content-Type", "application/json"),
            });
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
