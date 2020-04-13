package edu.gatech.cs6301.Backend3;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6301.Backend3.Model.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UsersUserIdProjectsProjectIdReport {
    private final String SESSION_START = "2019-02-18T20:00Z";
    private final String SESSION_END = "2019-02-18T20:30Z";
    private boolean setupdone = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    Util util;

    @Before
    public void runBefore() throws IOException {
        if (!setupdone) {
            System.out.println("*** SETTING UP TESTS ***");
            util = new Util();
            setupdone = true;
        }
        util.deleteAllUsers();
        System.out.println("*** STARTING TEST ***");
    }

    @After
    public void runAfter() {
        try {
            util.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Test get report from a non-existing user
     * Should return 404
     */
    public void getReportNonExistingUser() throws IOException {
        CloseableHttpResponse response = util.getReport("0", "0", "", "", false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test get report from an invalid user
     * Should return 404
     */
    public void getReportInvalidUser() throws IOException {
        CloseableHttpResponse response = util.getReport("!!", "0", "", "", false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test get report from a non-existing project
     * Should return 404
     */
    public void getReportNonExistingProject() throws IOException {
        User user = util.addUserSuccess();
        CloseableHttpResponse response = util.getReport(user.getId() + "", "0", "", "", false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test gen report from an invalid project id
     * Should return 404
     */
    public void getReportInvalidProject() throws IOException {
        User user = util.addUserSuccess();
        CloseableHttpResponse response = util.getReport(user.getId() + "", "!!", "", "", false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.NOT_FOUND);
        response.close();
    }

    @Test
    /**
     * Test gen report from an invalid start time
     * Should return 400
     */
    public void getReportInvalidStartTime() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());
        CloseableHttpResponse response = util.getReport(user.getId() + "", project.getId() + "", "!!", SESSION_END, false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test gen report from an invalid end time
     * Should return 400
     */
    public void getReportInvalidEndTime() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId());
        CloseableHttpResponse response = util.getReport(user.getId() + "", project.getId() + "", SESSION_START, "!!", false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.BAD_REQUEST);
        response.close();
    }

    @Test
    /**
     * Test gen report include pomodoros count and hours
     * Should return 200 and the correct report
     */
    public void getReportIncludePIncludeH() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T20:00Z", "2019-02-18T20:30Z", 2);
        Session session2 = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T21:00Z", "2019-02-18T21:30Z", 3);

        ReportSession reportSession1 = new ReportSession("2019-02-18T20:00Z", "2019-02-18T20:30Z", 0.5);
        ReportSession reportSession2 = new ReportSession("2019-02-18T21:00Z", "2019-02-18T21:30Z", 0.5);
        ReportSession[] reportSessions = {reportSession1, reportSession2};
        Report correctReport = new Report(reportSessions, 5, 1.0);

        CloseableHttpResponse response = util.getReport(user.getId() + "", project.getId() + "", "2019-02-18T20:00Z", "2019-02-18T21:30Z", true, true);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responseBody = EntityUtils.toString(response.getEntity());
        Report responseReport = objectMapper.readValue(responseBody, Report.class);
        assert(responseReport.equals(correctReport));
        response.close();
    }

    @Test
    /**
     * Test gen report not include pomodoros count, include hours
     * Should return 200 and the correct report
     */
    public void getReportNoPIncludeH() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T20:00Z", "2019-02-18T20:30Z", 2);
        Session session2 = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T21:00Z", "2019-02-18T21:30Z", 3);

        ReportSession reportSession1 = new ReportSession("2019-02-18T20:00Z", "2019-02-18T20:30Z", 0.5);
        ReportSession reportSession2 = new ReportSession("2019-02-18T21:00Z", "2019-02-18T21:30Z", 0.5);
        ReportSession[] reportSessions = {reportSession1, reportSession2};
        Report correctReport = new Report(reportSessions, null, 1.0);

        CloseableHttpResponse response = util.getReport(user.getId() + "", project.getId() + "", "2019-02-18T20:00Z", "2019-02-18T21:30Z", false, true);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responseBody = EntityUtils.toString(response.getEntity());
        Report responseReport = objectMapper.readValue(responseBody, Report.class);
        assert(responseReport.equals(correctReport));
        response.close();
    }

    @Test
    /**
     * Test gen report include pomodoros count, not include hours
     * Should return 200 and the correct report
     */
    public void getReportIncludePNoH() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T20:00Z", "2019-02-18T20:30Z", 2);
        Session session2 = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T21:00Z", "2019-02-18T21:30Z", 3);

        ReportSession reportSession1 = new ReportSession("2019-02-18T20:00Z", "2019-02-18T20:30Z", 0.5);
        ReportSession reportSession2 = new ReportSession("2019-02-18T21:00Z", "2019-02-18T21:30Z", 0.5);
        ReportSession[] reportSessions = {reportSession1, reportSession2};
        Report correctReport = new Report(reportSessions, 5, null);

        CloseableHttpResponse response = util.getReport(user.getId() + "", project.getId() + "", "2019-02-18T20:00Z", "2019-02-18T21:30Z", true, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responseBody = EntityUtils.toString(response.getEntity());
        Report responseReport = objectMapper.readValue(responseBody, Report.class);
        assert(responseReport.equals(correctReport));
        response.close();
    }

    @Test
    /**
     * Test gen report without options
     * Should return 200 and the correct report
     */
    public void getReportNoPNoH() throws IOException {
        User user = util.addUserSuccess();
        Project project = util.createProjectSuccess(user.getId());
        Session session = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T20:00Z", "2019-02-18T20:30Z", 2);
        Session session2 = util.createSessionSuccess(user.getId(), project.getId(), "2019-02-18T21:00Z", "2019-02-18T21:30Z", 3);

        ReportSession reportSession1 = new ReportSession("2019-02-18T20:00Z", "2019-02-18T20:30Z", 0.5);
        ReportSession reportSession2 = new ReportSession("2019-02-18T21:00Z", "2019-02-18T21:30Z", 0.5);
        ReportSession[] reportSessions = {reportSession1, reportSession2};
        Report correctReport = new Report(reportSessions, null, null);

        CloseableHttpResponse response = util.getReport(user.getId() + "", project.getId() + "", "2019-02-18T20:00Z", "2019-02-18T21:30Z", false, false);
        assert (response.getStatusLine().getStatusCode() == HttpStatusCode.SUCCESS);

        String responseBody = EntityUtils.toString(response.getEntity());
        Report responseReport = objectMapper.readValue(responseBody, Report.class);
        assert(responseReport.equals(correctReport));
        response.close();
    }
}
