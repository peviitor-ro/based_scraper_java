package com.peviitor.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class godaddy {
    public static JSONObject company = new JSONObject().put("company", "GoDaddy");
    public static String country = "Romania";

    public static void main(String[] args) {
        Document document = Jsoup.parse("");

        try {
            String request = utils.get(
                    "https://careers.godaddy.com/search-jobs/results?ActiveFacetID=Romania+-+Remote&CurrentPage=1&RecordsPerPage=15&Distance=50&RadiusUnitType=0&ShowRadius=False&IsPagination=False&FacetTerm=798549&FacetType=2&FacetFilters%5B0%5D.ID=Romania+-+Remote&FacetFilters%5B0%5D.FacetType=5&FacetFilters%5B0%5D.Count=2&FacetFilters%5B0%5D.Display=Romania+-+Remote&FacetFilters%5B0%5D.IsApplied=true&FacetFilters%5B0%5D.FieldName=custom_fields.PrimaryLocation&FacetFilters%5B1%5D.ID=798549&FacetFilters%5B1%5D.FacetType=2&FacetFilters%5B1%5D.Count=4&FacetFilters%5B1%5D.Display=Romania&FacetFilters%5B1%5D.IsApplied=true&FacetFilters%5B1%5D.FieldName=&SearchResultsModuleName=Search+Results&SearchFiltersModuleName=Search+Filters&SortCriteria=0&SortDirection=0&SearchType=3&OrganizationIds=7795&ResultsType=0",
                    new JSONObject[] {
                            new JSONObject().put("User-Agent", "Mozilla/5.0")
                    });
            JSONObject response = new JSONObject(request);
            document = Jsoup.parse(response.getString("results"));
        } catch (Exception e) {
            System.out.println(e);
        }

        Elements jobElements = document.getElementById("search-results-list").getElementsByTag("ul").last()
                .getElementsByTag("li");
        ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();

        for (Element element : jobElements) {
            JSONObject job = new JSONObject();

            job.put("id", UUID.randomUUID().toString());
            job.put("job_title", element.getElementsByTag("h2").text());
            job.put("job_link", "https://careers.godaddy.com" + element.getElementsByTag("a").attr("href"));
            job.put("company", company.getString("company"));
            job.put("country", country);
            job.put("city", "Romania");

            jobs.add(job);
        }

        System.out.println(JSONObject.valueToString(jobs));

        try {
            // // Clean Data from API
            utils.post("https://api.peviitor.ro/v4/clean/", JSONObject.valueToString(company), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/x-www-form-urlencoded").put("apikey",
                            "182b157-bb68-e3c5-5146-5f27dcd7a4c8")
            });

            // // Add Data to API
            utils.post("https://api.peviitor.ro/v4/update/", JSONObject.valueToString(jobs), new JSONObject[] {
                    new JSONObject().put("Content-Type", "application/json").put("apikey",
                            "182b157-bb68-e3c5-5146-5f27dcd7a4c8")
            });

            // Add logo
            String logo = "https://tbcdn.talentbrew.com/company/7795/v2_0/img/go-daddy-logo-165-34-withnomark.svg";
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
