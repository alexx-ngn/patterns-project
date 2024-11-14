package model;

import lombok.*;
import java.sql.Date;

@Getter
@Setter
public class PostReport extends Report {
    private int reportedPostId;

    // Constructor for PostReport that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public PostReport(int id, int adminId, int reportingUserId, Status status, String reason, Date dateReported, int reportedPostId) {
        super(id, adminId, reportingUserId, status, reason, dateReported);
        this.reportedPostId = reportedPostId;
    }

    public PostReport(int reportingUserId, Status status, String reason) {
        super(reportingUserId, status, reason);
    }
}
