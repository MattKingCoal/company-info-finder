package com.match.ods.dcf.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.match.ods.dcf.exception.CompanyNotFoundException;
import com.match.ods.dcf.exception.DocNotFoundException;

public class DBService {

    static Logger log = Logger.getLogger(DBService.class);
    private Connection conn;
    private PreparedStatement ps1, ps2;

    public DBService(String environment) {
        try {
            /*
             * Add db config details here
             */
            switch (environment) {
                case "PRO":
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@gcu12709.austin.hp.com:1526/KMODSPC", "ods3",
                            "ods3_20090916");
                    break;
                default:
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@gcu12007.austin.hp.com:1526/KMODSI", "ods3",
                            "seeker_20111102");
            }
            if (conn == null) {
                // TODO throw up
                log.error("Connection could not be esatblished. Connection - " + conn);
                System.exit(-1);
            }
            ps1 = conn.prepareStatement(SqlQueries.MDPROPSELECTCOMPANYINFOBYDOCKY);
            ps2 = conn.prepareStatement(SqlQueries.MDSELECTSTARBYDOCIDORDERBYVERSION);
            log.info("ODS Connection created");
        } catch (SQLException e) {
            log.error(e.getMessage());
            System.exit(-1);
        }
    }

    public String getCompanyInfoByDocKey(Integer key) throws CompanyNotFoundException {
        log.debug("Searching for companyInfo for key " + key);
        try {
            ps1.setInt(1, key);
            ResultSet rs = ps1.executeQuery();
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
            ps2.setString(1, docid + "%");
            rs = ps2.executeQuery();
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
            if (ps1 != null)
                ps1.close();
            if (ps2 != null)
                ps2.close();
            if (conn != null)
                conn.close();
            log.info("DB Resources released");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
}
