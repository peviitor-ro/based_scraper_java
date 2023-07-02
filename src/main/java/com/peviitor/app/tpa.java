package com.peviitor.app;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class tpa {
    public static JSONObject company = new JSONObject().put("company", "tpa");
    public static String country = "Romania";
    public static void main(String[] args) {
        Document document = Jsoup.parse("");
        try {
            document = Jsoup.connect("https://www.tpa-group.ro/en/job-openings-keep-in-touch/").get();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();
        
        Elements jobElements = document.getElementsByClass("entry-content clearfix").first().getElementsByTag("li");
        for (Element element : jobElements) {
            JSONObject job = new JSONObject();

            job.put("id", UUID.randomUUID().toString());
            job.put("job_title", element.getElementsByTag("a").text());
            job.put("job_link", element.getElementsByTag("a")
                                            .attr("href"));
            job.put("company", company.getString("company"));
            job.put("country", country);
            job.put("city", "Romania");

            jobs.add(job);
        }

        System.out.println(JSONObject.valueToString(jobs));
        System.out.println(jobs.size());

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
            String logo = "https://www.tpa-group.ro/wp-content/uploads/sites/6/2017/12/TPA-Tax-Audit-CEE-Logo.png";
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
