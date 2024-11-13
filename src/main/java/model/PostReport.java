package model;

import lombok.*;

@Getter
@Setter
public class PostReport extends Report {
    private int reportedPostId;

    public PostReport(int reportingUserId, Status status, String reason) {
        super(reportingUserId, status, reason);
    }
}
