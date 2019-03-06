package com.match.ods.dcf.data;

public class SqlQueries {

    public static final String MDSELECTSTARBYDOCIDORDERBYVERSION = "select * from MD where doc_id like ? order by VERS_NR desc";
    public static final String MDPROPSELECTCOMPANYINFOBYDOCKY = "select ctrl_vcblry_node_ky from md_prop where prop_ky = 2061 and md_doc_ky = ?";
}
