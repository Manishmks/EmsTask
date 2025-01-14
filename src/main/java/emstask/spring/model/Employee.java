package emstask.spring.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
public class Employee
{
    public Employee(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int empId;

    @Transient
    String desgName;

    @OneToOne
    @JoinColumn(name = "des_ID")
    @JsonIgnore
    public Designation designation;

    public String getDesgName() {
        return designation.desgName;
    }

    public void setDesgName(String desgName) {
        this.desgName = desgName;
    }

    @JsonIgnore
    @Column(nullable = true)
    private Integer parentId;

    String empName;

    public Employee(Designation designation, Integer parentId, String empName)
    {
        this.designation=designation;
        this.parentId=parentId;
        this.empName=empName;
    }

    public int getEmpId()
    {
        return empId;
    }

    public Designation getDesignation()
    {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", designation=" + designation +
                ", parentId=" + parentId +
                ", empName='" + empName + '\'' +
                '}';
    }
}
