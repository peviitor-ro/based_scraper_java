package com.peviitor.app;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class main {
    public static void main(String[] args) throws Exception {
        
        Path currePath = FileSystems.getDefault().getPath("").toAbsolutePath();
        String currentPathString = currePath.toString();
        Process p = Runtime.getRuntime().exec("ls " + currentPathString + "/src/main/java/com/peviitor/app/");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        ArrayList<String> files = new ArrayList<String>();

        // exclude files
        List<String> exclude = new ArrayList<String>() {{
            add("main.java");
            add("utils.java");
        }};

        // get all files
        for (String line = stdInput.readLine(); line != null; line = stdInput.readLine()) {
            if (line.contains(".java") && exclude.contains(line) == false){
                files.add(line.replace(".java", ""));
            }
           
        }

        // run all scrapers
        for (String file : files) {
            System.out.println("Running test: " + file);
            Process run = Runtime.getRuntime().exec("mvn compile exec:java -Dexec.mainClass=com.peviitor.app." + file);
            BufferedReader scraperInput = new BufferedReader(new InputStreamReader(run.getInputStream()));
            for (String line = scraperInput.readLine(); line != null; line = scraperInput.readLine()) {
                System.out.println(line);
            }
        }
    }
}

