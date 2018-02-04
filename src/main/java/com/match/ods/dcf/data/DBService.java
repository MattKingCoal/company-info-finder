package com.match.ods.dcf.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.match.ods.dcf.exception.CompanyNotFoundException;
import com.match.ods.dcf.exception.DocNotFoundException;

public class DBService {

    static Logger log = Logger.getLogger(DBService.class);
    private Connection conn;
    private Statement statement;

    public DBService(String environment) {
        try {
            /*
             * Add db config details here
             */
            switch (environment) {
                case "PRO":
                    conn = DriverManager.getConnection("");
                    break;
                default:
                    conn = DriverManager.getConnection("");
            }
            if (conn == null) {
                // TODO throw up
                log.error("Connection could not be esatblished. Connection - " + conn);
                System.exit(-1);
            }

            statement = conn.createStatement();
            log.info("ODS Connection created");
        } catch (SQLException e) {
            log.error(e.getMessage());
            System.exit(-1);
        }
    }

    public String getCompanyInfoByDocKey(Integer key) throws CompanyNotFoundException {
        log.debug("Searching for companyInfo for key " + key);
        try {
            ResultSet rs = statement.executeQuery(
                    "select ctrl_vcblry_node_ky from md_prop where prop_ky = 2061 and md_doc_ky = " + key);
            if (rs.next()) {
                int val = rs.getInt(1);
                if (val == 41002) {
                    log.debug("Company is HPI");
                    return "HPI";
                } else if (val == 41001) {
                    log.debug("Company is HPE");
                    return "HPE";
                }
            } else {
                log.debug("No company found");
                throw new CompanyNotFoundException("Company not found  - " + key);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return "";
    }

    public Integer getDocKeyByDocId(String docid) throws DocNotFoundException {
        log.debug("Searching for doc_ky for: " + docid);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(("select * from MD where doc_id like '" + docid + "%' order by VERS_NR desc"));
            if (rs.next()) {
                Integer i = rs.getInt(1);
                log.debug("Found key: " + i + " for: " + docid);
                return i;
            } else {
                throw new DocNotFoundException("No such document - " + docid);
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return 0;
    }

    public void close() {
        try {
            if (statement != null)
                statement.close();
            if (conn != null)
                conn.close();
            log.info("DB Resources released");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
}
