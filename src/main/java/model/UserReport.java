package model;

public class UserReport extends Report{
    private int reportedUserId;

    public UserReport(int reportingUserId, Status status, String reason) {
        super(reportingUserId, status, reason);
    }
}
