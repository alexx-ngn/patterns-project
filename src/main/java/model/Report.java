package model;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
public abstract class Report {
    private int id;
    private int adminId;
    private int reportingUserId;
    private String reason;
    private Status status;
    private Date dateReported;

    public enum Status {
        CREATED, ASSIGNED, PROCESSING, BLOCKED, CLOSED
    }

    // Constructor for Report that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public Report(int id, int adminId, int reportingUserId, Status status, String reason, Date dateReported) {
        this.id = id;
        this.adminId = adminId;
        this.reportingUserId = reportingUserId;
        this.status = status;
        this.reason = reason;
        this.dateReported = dateReported;
    }

    public Report(int reportingUserId, Status status, String reason) {
        this.status = status;
        this.reason = reason;
        this.reportingUserId = reportingUserId;
        // TODO: adminId assigned in another method, Report.assignAdmin method ??
        // id, dateReported, handled in database
    }
}
