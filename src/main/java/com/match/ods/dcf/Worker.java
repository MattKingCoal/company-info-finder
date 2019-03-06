package com.match.ods.dcf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.match.ods.dcf.data.DBService;
import com.match.ods.dcf.exception.CompanyNotFoundException;
import com.match.ods.dcf.exception.DocNotFoundException;

public class Worker implements Runnable {

    static Logger log = Logger.getLogger(Worker.class);

    private DBService dbs;
    private ConcurrentLinkedQueue<String> docIDQueue;
    private List<Doc> docs = new ArrayList<>();
    private boolean completed = false;
    private Thread thread;
    private String name;

    public Worker(String name, DBService dbs, ConcurrentLinkedQueue<String> docIDQueue) {
        this.dbs = dbs;
        this.docIDQueue = docIDQueue;
        this.name = name;
    }

    @Override
    public void run() {
        String id;
        while ((id = docIDQueue.poll()) != null) {
            log.info(name + " - Searching for document " + id);
            Integer key = null;
            try {
                key = dbs.getDocKeyByDocId(id);
            } catch (DocNotFoundException e) {
                log.error(name + " - " + e.getMessage());
                continue;
            }
            String company = null;
            try {
                company = dbs.getCompanyInfoByDocKey(key);
            } catch (CompanyNotFoundException e) {
                log.error(name + " - " + e.getMessage());
                continue;
            }
            Doc d = new Doc(id, key, company);
            docs.add(d);
            log.info(name + " - Document found: " + d);
        }
        log.info(name + " - Drain - Done");
        completed = true;
        dbs.close();
    }

    public void start() {
        thread = new Thread(this, name);
        thread.start();
    }

    public List<Doc> getDocs() {
        return docs;
    }

    public boolean isCompleted() {
        return completed;
    }
}
