package com.peviitor.app;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class expleo {
    public static JSONObject company = new JSONObject().put("company", "Expleo");
    public static String country = "Romania";
    public static void main(String[] args) {
        Document document = Jsoup.parse("");
        try {
            document = Jsoup.connect("https://careers-expleo-jobs.icims.com/jobs/search?pr=0&in_iframe=1&searchLocation=13526").get();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        String[] pages = document.getElementsByClass("iCIMS_SearchResultsHeader").first()
            .getElementsByTag("h2").first().text().split(" ");

        int totalpages = Integer.parseInt(pages[pages.length - 1]);
        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();

        for (int i = 1; i <= totalpages; i++) {
            int page = i - 1;
            try {
                document = Jsoup.connect("https://careers-expleo-jobs.icims.com/jobs/search?pr=" + page +"&in_iframe=1&searchLocation=13526").get();
            }
            catch (Exception e) {
                System.out.println(e);
            }

            Elements jobElements = document.getElementsByClass("iCIMS_JobsTable").first().getElementsByClass("row");
            for (Element element : jobElements) {
                JSONObject job = new JSONObject();

                job.put("id", UUID.randomUUID().toString());
                job.put("job_title", element.getElementsByClass("title").text());
                job.put("job_link", element.getElementsByClass("iCIMS_Anchor")
                                                .first()
                                                .attr("href"));
                job.put("company", company.getString("company"));
                job.put("city", element.getElementsByClass("iCIMS_JobHeaderData")
                                                .first()
                                                .text()
                                                .split("-")[2]
                                                .replace(" | RO", ""));
                job.put("country", country);

                jobs.add(job);
            }
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
            String logo = "https://expleo.com/global/en/wp-content/uploads/2019/02/expleo-logo-and-tagline.png";
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
