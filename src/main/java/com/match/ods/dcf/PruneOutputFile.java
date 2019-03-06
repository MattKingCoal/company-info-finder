package com.match.ods.dcf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class PruneOutputFile {

    public static void main(String[] args) throws IOException {
        File in = new File("output", "balls.txt");
        File out = new File("output", "final");

        BufferedReader reader = new BufferedReader(new FileReader(in));
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        String line;
        while ((line = reader.readLine()) != null) {
            String s = StringUtils.substringAfter(line, "Document [ID=");
            s = StringUtils.substringBefore(s, ", DocKey=");
            System.out.println(s);
            writer.write(s);
            writer.newLine();
        }
        reader.close();
        writer.close();
    }

}
