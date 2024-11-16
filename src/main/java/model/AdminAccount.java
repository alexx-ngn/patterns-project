package model;

import lombok.*;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
public class AdminAccount extends Account {
    private Queue<Report> assignedReports;

    public AdminAccount(String name, String email, String username) {
        super(name, email, username);
        this.assignedReports = new LinkedList<>(); // TODO: Implement own queue for this?
    }

    @Override
    public void removePost(Post post) {

    }

    // TODO: public void removeReport(Report ticket)

    // TODO: public void ban(UserAccount account)

    // TODO: public void fetchNewReport(), *** shouldn't it be void since it'll just add a report to this.assignedReports

    // TODO: public void changeReportStatus(Report report, Status status)

    // TODO: public void assignReportTo(AdminAccount admin, Report report)

    // TODO: public void closeReport(Report report)
}
