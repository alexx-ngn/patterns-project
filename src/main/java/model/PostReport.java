package model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
public class PostReport extends Report {
    private int reportedPostId;
    private static int nextId;

    // Constructor for PostReport that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public PostReport(int id, int reportingUserId, Status status, String reason, Date dateReported, int reportedPostId) {
        super(id, reportingUserId, status, reason, dateReported);
        this.reportedPostId = reportedPostId;
    }

    public PostReport(int reportingUserId, Status status, String reason, int reportedPostId) {
        super(reportingUserId, status, reason);
        nextId = ReportSystem.getInstance().getPostReports().isEmpty() ? 1 :
                ReportSystem.getInstance().getPostReports().getLast().getId() + 1;
        this.setId(nextId++);
        this.reportedPostId = reportedPostId;
    }
}
