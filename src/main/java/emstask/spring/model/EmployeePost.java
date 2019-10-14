package emstask.spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeePost
{
    @JsonProperty("name")
    String name =null;
    @JsonProperty("jobTitle")
    String jobTitle =null;
    @JsonProperty("managerId")
    Integer managerId=null;

    public EmployeePost(){}
    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public EmployeePost(String name, String jobTitle, Integer managerId) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.managerId = managerId;
    }
}

