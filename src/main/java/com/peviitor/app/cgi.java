package com.peviitor.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class cgi {
    public static JSONObject company = new JSONObject().put("company", "CGI");
    public static String country = "Romania";

    public static void main(String[] args){
        Document document = Jsoup.parse("");
        JSONObject data = new JSONObject();

        data.put("inp_City", "Bucharest");
        try {
            String request = utils.post("https://cgi.njoyn.com/corp/xweb/XWeb.asp?NTKN=c&clid=21001&Page=joblisting",JSONObject.valueToString(data), new JSONObject[] {
                new JSONObject().put("Content-Type", "application/x-www-form-urlencoded")
            });
            document = Jsoup.parse(request);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();

        Elements jobElements = document.getElementsByClass("views-table")
                                .first()
                                .getElementsByTag("tbody")
                                .first()
                                .getElementsByTag("tr");

        for (Element element : jobElements) {
            JSONObject job = new JSONObject();

            String city = element.getElementsByTag("td").get(3).text();

            if (city.toLowerCase().equals("bucharest")){
                job.put("id", UUID.randomUUID().toString());
                job.put("job_title", element.getElementsByTag("td").get(1).text());
                job.put("job_link", "https://cgi.njoyn.com/corp/xweb/" +
                                        element.getElementsByTag("a")
                                            .attr("href"));
                job.put("company", company.getString("company"));
                job.put("country", country);
                job.put("city", city);

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
            String logo = "https://www.cgi.com/sites/default/files/cgi-logo-red.jpg";
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
