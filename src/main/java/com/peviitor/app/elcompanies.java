package com.peviitor.app;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;

public class elcompanies {
    public static JSONObject company = new JSONObject().put("company", "Elcompanies");
    public static String country = "Romania";

    public static void main(String[] args) {
        ArrayList<JSONObject> jobElements = new ArrayList<JSONObject>();
        int page = 1;
        try {
            while (true) {
                String request = utils.get(
                        "https://jobs.elcompanies.com/api/jobs?page=" +
                                    page +
                        "&location=romania&_ga=2.200794984.736046121.1688495059-474725147.1688495059&_gl=1*18za4zx*_ga*NDc0NzI1MTQ3LjE2ODg0OTUwNTk.*_ga_V9QZ4PSDRY*MTY4ODQ5NTA1OS4xLjAuMTY4ODQ5NTA1OS42MC4wLjA.&stretch=10&stretchUnit=MILES&sortBy=relevance&descending=false&internal=false",
                        new JSONObject[] {
                                new JSONObject().put("User-Agent", "Mozilla/5.0")
                        });
                JSONObject response = new JSONObject(request);
                if (response.getJSONArray("jobs").length() == 0) {
                    break;
                }
                for (Object element : response.getJSONArray("jobs")) {
                    jobElements.add((JSONObject) element);
                }
                page++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();
        System.out.println(jobElements.size());

        for (JSONObject element : jobElements) {
            JSONObject obj = element.getJSONObject("data");
            JSONObject job = new JSONObject();

            job.put("id", UUID.randomUUID().toString());
            job.put("job_title", obj.getString("title"));
            job.put("job_link", obj.getJSONObject("meta_data").getString("canonical_url"));
            job.put("company", company.getString("company"));
            job.put("country", country);
            try {
                job.put("city", obj.getString("city"));
            } catch (Exception e) {
                System.out.println(e);
            }
            jobs.add(job);
        }

        System.out.println(JSONObject.valueToString(jobs));

        try {
            String apikey = System.getenv("APIKEY");
            // // Clean Data from API
            utils.post("https://api.peviitor.ro/v4/clean/", JSONObject.valueToString(company), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/x-www-form-urlencoded").put("apikey",
                            apikey)
            });

            // // Add Data to API
            utils.post("https://api.peviitor.ro/v4/update/", JSONObject.valueToString(jobs), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/json").put("apikey",
                            apikey)
            });

            // Add logo
            String logo = "https://logos-world.net/wp-content/uploads/2020/12/Estee-Lauder-Logo.png";
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
