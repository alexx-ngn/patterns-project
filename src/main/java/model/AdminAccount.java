package model;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
public class AdminAccount extends Account {
    private Queue<Report> assignedReports;

    // Constructor for AdminAccount that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public AdminAccount(int id, String name, String email, String username, Date creationDate) {
        super(id, name, email, username, creationDate);
        this.assignedReports = new LinkedList<>();
    }

    public AdminAccount(String name, String email, String username) {
        super(name, email, username);
        this.assignedReports = new LinkedList<>(); // TODO: Implement own queue for this?
    }

    // TODO: public void removeReport(Report ticket)

    // TODO: public void ban(UserAccount account)

    // TODO: public void fetchNewReport(), *** shouldn't it be void since it'll just add a report to this.assignedReports

    // TODO: public void changeReportStatus(Report report, Status status)

    // TODO: public void assignReportTo(AdminAccount admin, Report report)

    // TODO: public void closeReport(Report report)
}
