package emstask.spring.model;

public class EmployeePost
{
    String empName=null;
    String empDesg=null;
    Integer parentId=null;

    public EmployeePost(){}
    public String getEmpName()
    {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpDesg() {
        return empDesg;
    }

    public void setEmpDesg(String empDesg) {
        this.empDesg = empDesg;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public EmployeePost(String empName, String empDesg, Integer parentId) {
        this.empName = empName;
        this.empDesg = empDesg;
        this.parentId = parentId;
    }
}

