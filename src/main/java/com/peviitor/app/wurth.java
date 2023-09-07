package com.peviitor.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class wurth {
    public static JSONObject company = new JSONObject().put("company", "Wurth");
    public static String country = "Romania";

    public static void main(String[] args){
        Document document = null;
        JSONObject data = new JSONObject();

        try {
            document = Jsoup.connect("https://www.wuerth.ro/articole/cariere.html").get();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();

        Elements jobElements = document.getElementsByClass("submenu").get(0).getElementsByTag("a");

        for (Element element : jobElements) {
            JSONObject job = new JSONObject();


                job.put("job_title", element.text());
                job.put("job_link", "https://www.wuerth.ro" + element.attr("href"));
                job.put("company", company.getString("company"));
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
            String logo = "https://cdn.contentspeed.ro/slir/h60/wuerth100.websales.ro/cs-content//cs-skins/cs_lider/custom/logo-image_1550142223.png";
            ArrayList<JSONObject> logoData = new ArrayList<JSONObject>();
            logoData.add(new JSONObject().put("id", company.getString("company")).put("logo", logo));

            utils.post("https://api.peviitor.ro/v1/logo/add/", JSONObject.valueToString(logoData), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/json"),
            });
        } catch (Exception e) {
            System.out.println(e);
         }
    }
}

