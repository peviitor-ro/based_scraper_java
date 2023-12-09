package com.peviitor.app;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThyssenkruppBilstein {
    private static final List<JSONObject> jobsList = new ArrayList<>();
    public static JSONObject company = new JSONObject().put("company", "ThyssenkruppBilstein");
 

    public static void main(String[] args) throws IOException {
        String url = "https://thyssenkruppromania.teamtailor.com/jobs";
        parsePage(url);
        url = "https://thyssenkruppromania.teamtailor.com/jobs/show_more?page=2";
        parsePage(url);


        try {
            String apikey = System.getenv("MADAIR");
            // // Clean Data from API
            utils.post("https://api.peviitor.ro/v4/clean/", JSONObject.valueToString(company), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/x-www-form-urlencoded")
                    .put("apikey", apikey)
            });

            // // Add Data to API
            utils.post("https://api.peviitor.ro/v4/update/", JSONObject.valueToString(jobsList), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/json")
                    .put("apikey", apikey)
            });

            // Add logo
            String logo = "https://images.teamtailor-cdn.com/images/s3/teamtailor-production/logotype-v3/image_uploads/b6766961-2e5c-4377-b17e-830a53993e02/original.png";
            ArrayList<JSONObject> logoData = new ArrayList<JSONObject>();
            logoData.add(new JSONObject().put("id", company.getString("company")).put("logo", logo));

            utils.post("https://api.peviitor.ro/v1/logo/add/", JSONObject.valueToString(logoData), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/json"),
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void parsePage(String url) throws IOException{
        Document document = Jsoup.connect(url).get();
        Elements elements = document.getElementsByClass("block-grid");
        for (Element e : elements.select("li")){
            populateJobsList(e.getElementsByTag("span").attr("title"),
                            e.getElementsByTag("a").attr("href"));
        }
    }

    public static void populateJobsList(String jobTitle, String jobLink){
        JSONObject job = new JSONObject();
        job.put("job_title", jobTitle);
        job.put("job_link", jobLink);
        job.put("company", "ThyssenkruppBilstein");
        job.put("country", "Romania");
        job.put("city", "Sibiu");
        job.put("county", "Sibiu");
        jobsList.add(job);

    }


}
