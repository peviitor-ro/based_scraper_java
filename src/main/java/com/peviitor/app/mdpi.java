package com.peviitor.app;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;


public class mdpi {
    public static JSONObject company = new JSONObject().put("company", "MDPI");
    public static String country = "Romania";
    public static void main(String[] args) {
        Document document = Jsoup.parse("");
        try {
            document = Jsoup.connect("https://careers.ro.mdpi.com/jobs?query=").get();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();
        
        Elements jobElements = document.getElementsByClass("z-career-job-card-image");
        for (Element element : jobElements) {
            JSONObject job = new JSONObject();

            job.put("id", UUID.randomUUID().toString());
            job.put("job_title", element.getElementsByClass("text-block-base-link").text());
            job.put("job_link", element.getElementsByTag("a")
                                            .attr("href"));
            job.put("company", company.getString("company"));
            job.put("country", country);
            job.put("city", element.getElementsByClass("mt-1 text-md")
                                        .first()   
                                        .getElementsByTag("span")
                                        .last()
                                        .text());

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
            String logo = "https://images.teamtailor-cdn.com/images/s3/teamtailor-production/logotype-v3/image_uploads/9a39cb44-b1c9-4c5e-8282-9256dd0e070d/original.png";
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
