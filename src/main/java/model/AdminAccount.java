package model;

import lombok.*;

import java.sql.Date;
import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
public class AdminAccount extends Account {
    private Queue<Report> assignedReports;

    // Constructor for AdminAccount that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public AdminAccount(int id, String name, String email, String username, String password, Date creationDate) {
        super(id, name, email, username, password, creationDate);
        this.assignedReports = new LinkedList<>();
    }

    public AdminAccount(String name, String email, String username, String password) {
        super(name, email, username, password);
        this.assignedReports = new LinkedList<>();
    }

    @Override
    public void removePost(Post post) {
        // TODO: not needed? Because all that must be done is for the post to be removed from UserSystem an DB,
        //  so admin doesn't really have to do anything
    }

    public void removeReport(Report report) {
        assignedReports.remove(report);
    }

    public void ban(UserAccount account) {
        // TODO: again all is done thru UserSystemeController so idk man
    }

    public void fetchNewReport(Report report) {
        // TODO: I don't think we need this method because everything is done through GUI for admin,
    }

    public void changeReportStatus(Report report, Report.Status status) {
        report.setStatus(status);
    }

    public void assignReportTo(AdminAccount admin, Report report) {
        assignedReports.remove(report);
        admin.getAssignedReports().add(report);
    }

    public void closeReport(Report report) {
        changeReportStatus(report, Report.Status.CLOSED);
        removeReport(report);
    }
}
