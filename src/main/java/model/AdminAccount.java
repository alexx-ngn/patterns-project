package model;

import lombok.*;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
public class AdminAccount extends Account {
    private Queue<Report> assignedReports;

    // Constructor for AdminAccount that handles all fields, this is used for fetching data from the database and
    // creating an object. Used for creating a list when selecting all from the database.
    public AdminAccount(int id, String name, String email, String username, String password) {
        super(id, name, email, username, password);
        this.assignedReports = new LinkedList<>();
    }

    public AdminAccount(String name, String email, String username, String password) {
        super(name, email, username, password);
        this.assignedReports = new LinkedList<>();
    }

    /**
     * Removes a specified report from the list of assigned reports.
     *
     * @param report the report to be removed from the assigned reports queue
     */
    public void removeReport(Report report) {
        assignedReports.remove(report);
    }

    /**
     * Changes the status of the specified report to the given status.
     *
     * @param report the report whose status is to be changed
     * @param status the new status to be set for the report
     */
    public void changeReportStatus(Report report, Report.Status status) {
        report.setStatus(status);
    }

    /**
     * Assigns a report from the current admin account to another specified admin account.
     *
     * @param admin The AdminAccount to which the report is assigned.
     * @param report The Report to be assigned.
     */
    public void assignReportTo(AdminAccount admin, Report report) {
        assignedReports.remove(report);
        admin.getAssignedReports().add(report);
    }

    /**
     * Closes the specified report by changing its status to CLOSED and
     * removing it from the list of assigned reports.
     *
     * @param report the report to be closed
     */
    public void closeReport(Report report) {
        changeReportStatus(report, Report.Status.CLOSED);
        removeReport(report);
    }
}
