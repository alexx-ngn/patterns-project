package model;

import lombok.*;

import java.util.Date;
@Getter
@Setter
public abstract class Report {
    private int id;
    private static int nextId;
    private int reportingUserId;
    private String reason;
    private Status status;
    private Date dateReported;

    public enum Status {
        OPENED, CLOSED
    }

    // Constructor for Report that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public Report(int id, int reportingUserId, Status status, String reason, Date dateReported) {
        this.id = id;
        this.reportingUserId = reportingUserId;
        this.status = status;
        this.reason = reason;
        this.dateReported = dateReported;
    }

    public Report(int reportingUserId, Status status, String reason) {
        this.status = status;
        this.reason = reason;
        this.dateReported = new Date(System.currentTimeMillis());
        this.reportingUserId = reportingUserId;
        // TODO: adminId assigned in another method, Report.assignAdmin method ??
        // id, dateReported, handled in database
    }
}
