package edu.gatech.cs6301.Backend3.Model;

import java.util.Objects;

public class Project {
    private int id;
    private String projectname;

    public Project() {
    }

    public Project(String projectname) {
        this.projectname = projectname;
    }

    public Project(int id, String projectname) {
        this.id = id;
        this.projectname = projectname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public boolean equalsExceptId(Project project) {
        return this.projectname.equals(project.getProjectname());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id &&
                Objects.equals(projectname, project.projectname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectname);
    }
}
