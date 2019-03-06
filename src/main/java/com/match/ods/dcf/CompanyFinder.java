package com.match.ods.dcf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.match.ods.dcf.data.DBService;

public class CompanyFinder {

    static Logger log = Logger.getLogger(CompanyFinder.class);
    private static List<Worker> workers = new ArrayList<>();
    private static ConcurrentLinkedQueue<String> docIdQueue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Starting............");
        String inputFilename = args[0];
        String outputFilename = args[1];
        String env = args[2];
        int numWorkers = Integer.parseInt(args[3]);
        if (!validEnv(env)) {
            log.error("Invalid environment.....");
            System.exit(-1);
        }

        // Read input file
        List<String> ids = readInputFile(inputFilename);
        log.info("File read, list created, we have " + ids.size() + " docs to search for");
        docIdQueue.addAll(ids);

        log.info("Creating threads.....");

        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker("CI" + i, new DBService(env), docIdQueue);
            workers.add(worker);
        }

        log.info("Starting threads.....");
        for (Worker worker : workers) {
            worker.start();
        }

        while (!areWorkersFinished()) {
            log.info("Workers still chipping away, sleeping............");
            log.info(String.format("%d docs in the queue", docIdQueue.size()));
            Thread.sleep(1000 * 10);
        }
        log.info("All workers finished");

        // Combine the docList from all workers
        List<Doc> fullDocList = new ArrayList<>();
        for (Worker w : workers) {
            fullDocList.addAll(w.getDocs());
        }

        /*
         * Remove non HPI docs Do it with a predicate
         */
        log.info("Time to remove non HPIs.......");
        fullDocList = filterWithPredicate(fullDocList);

        // Write final doc list to file
        File out = new File("output", outputFilename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        log.info("Final count of HPI docs is: " + fullDocList.size());
        log.info("Writing to output file: " + out.getAbsolutePath());
        for (Doc d : fullDocList) {
            writer.write(d.toString());
            writer.newLine();
        }
        writer.close();
        log.info("Boom Shakalaka!! All done. thanks for watching");
    }

    private static List<Doc> filterWithPredicate(List<Doc> fullDocList) {
        Predicate<Doc> predicate = new CompanyHPIPredicate();
        List<Doc> filtered = fullDocList.stream().filter(predicate).collect(Collectors.<Doc> toList());
        return filtered;
    }

    private static boolean areWorkersFinished() {
        for (Worker worker : workers) {
            if (!worker.isCompleted())
                return false;
        }
        return true;
    }

    private static List<String> readInputFile(String inputFilename) throws IOException {
        List<String> ids = new ArrayList<>();
        File f = new File("input", inputFilename);
        log.info("Reading input file " + f.getAbsolutePath());
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;
        while ((line = reader.readLine()) != null) {
            log.debug("Adding id to list: " + line);
            ids.add(line);
        }
        reader.close();
        return ids;
    }

    private static boolean validEnv(String env) {
        if ("PRO".equals(env) || "ITG".equals(env))
            return true;
        return false;
    }
}
