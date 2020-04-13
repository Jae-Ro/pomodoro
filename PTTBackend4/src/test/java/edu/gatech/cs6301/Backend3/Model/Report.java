package edu.gatech.cs6301.Backend3.Model;

import java.util.Arrays;
import java.util.Objects;

public class Report {
    private ReportSession[] sessions;
    private Integer completedPomodoros;
    private Double totalHoursWorkedOnProject;

    public Report() {
    }

    public Report(ReportSession[] sessions, Integer completedPomodoros, Double totalHoursWorkedOnProject) {
        this.sessions = sessions;
        this.completedPomodoros = completedPomodoros;
        this.totalHoursWorkedOnProject = totalHoursWorkedOnProject;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Arrays.equals(sessions, report.sessions) &&
                Objects.equals(completedPomodoros, report.completedPomodoros) &&
                Objects.equals(totalHoursWorkedOnProject, report.totalHoursWorkedOnProject);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(completedPomodoros, totalHoursWorkedOnProject);
        result = 31 * result + Arrays.hashCode(sessions);
        return result;
    }

    public ReportSession[] getSessions() {
        return sessions;
    }

    public void setSessions(ReportSession[] sessions) {
        this.sessions = sessions;
    }

    public Integer getCompletedPomodoros() {
        return completedPomodoros;
    }

    public void setCompletedPomodoros(Integer completedPomodoros) {
        this.completedPomodoros = completedPomodoros;
    }

    public Double getTotalHoursWorkedOnProject() {
        return totalHoursWorkedOnProject;
    }

    public void setTotalHoursWorkedOnProject(Double totalHoursWorkedOnProject) {
        this.totalHoursWorkedOnProject = totalHoursWorkedOnProject;
    }
}
