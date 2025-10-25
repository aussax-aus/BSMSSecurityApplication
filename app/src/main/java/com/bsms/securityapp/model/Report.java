package com.bsms.securityapp.model;

public class Report {
    private String siteName;
    private String siteAddress;
    private String date;
    private String officerName;
    private String startTime;
    private String finishTime;
    private String notes;

    // Getters
    public String getSiteName() { return siteName; }
    public String getSiteAddress() { return siteAddress; }
    public String getDate() { return date; }
    public String getOfficerName() { return officerName; }
    public String getStartTime() { return startTime; }
    public String getFinishTime() { return finishTime; }
    public String getNotes() { return notes; }

    // Setters
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public void setSiteAddress(String siteAddress) { this.siteAddress = siteAddress; }
    public void setDate(String date) { this.date = date; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setFinishTime(String finishTime) { this.finishTime = finishTime; }
    public void setNotes(String notes) { this.notes = notes; }
}
