package emstask.spring.model;

public class EmployeePut
{
    public EmployeePut()
    {

    }
    public EmployeePut(String empName, String empDesg, Integer parentId, boolean replace) {
        this.empName = empName;
        this.empDesg = empDesg;
        this.parentId = parentId;
        this.replace = replace;
    }

    String empName=null;
    String empDesg=null;
    Integer parentId=null;
    boolean replace=false;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

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
}
