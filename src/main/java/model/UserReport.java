package model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
public class UserReport extends Report{
    private int reportedUserId;

    // Constructor for UserReport that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public UserReport(int id, int reportingUserId, Status status, String reason, Date dateReported, int reportedUserId) {
        super(id, reportingUserId, status, reason, dateReported);
        this.reportedUserId = reportedUserId;
    }

    public UserReport(int reportingUserId, Status status, String reason) {
        super(reportingUserId, status, reason);
    }
}
