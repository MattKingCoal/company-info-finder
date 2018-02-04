package com.match.ods.dcf;

public class Doc {

    private String docId;
    private Integer docKey;
    private String company;

    public Doc(String id, Integer key, String company) {
        this(id);
        this.docKey = key;
        this.company = company;
    }

    public Doc(String id) {
        this.docId = id;
    }

    @Override
    public String toString() {
        return String.format("Document [ID=%s, DocKey=%s, Company=%s]", docId, docKey, company);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Doc other = (Doc) obj;
        if (docId == null) {
            if (other.docId != null)
                return false;
        } else if (!docId.equals(other.docId))
            return false;
        return true;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Integer getDocKey() {
        return docKey;
    }

    public void setDocKey(Integer docKey) {
        this.docKey = docKey;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
