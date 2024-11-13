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

    public Report(int reportingUserId, Status status, String reason) {
        this.status = status;
        this.reason = reason;
        this.reportingUserId = reportingUserId;
        // TODO: adminId assigned in another method, Report.assignAdmin method ??
        // id, dateReported, handled in database
    }
}
