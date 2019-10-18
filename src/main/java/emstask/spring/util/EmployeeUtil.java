package emstask.spring.util;

import emstask.spring.dao.DesignationRepository;
import emstask.spring.dao.EmployeeRepository;
import emstask.spring.model.Designation;
import emstask.spring.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class EmployeeUtil
{
    @Autowired
    public EmployeeRepository empRepo;
    @Autowired
    public DesignationRepository degRepo;

    // verifying entered designation is greater or equal to current designation of employee
    public boolean isGreaterThanEqualCurrentDesignation(Integer eid,String desg)
    {
        Employee employee=empRepo.findByEmpId(eid);
        float selfLevel=employee.getDesignation().getLevel();
        float parentLevel=degRepo.findByDesgName(desg).getLevel();
        if(selfLevel>=parentLevel)
            return true;
        else
            return false;
    }

    // verifying entered designation is not greater than its Manager designation
    public boolean isSmallerThanParent(Integer eid,String desg)
    {
        Employee employee=empRepo.findByEmpId(eid);
        if(employee.getParentId()!=null)
        {
            float parentLevel=empRepo.findByEmpId(employee.getParentId()).getDesignation().getLevel();
            float selfLevel=degRepo.findByDesgName(desg).getLevel();
            if(selfLevel>parentLevel)
                return true;
            else
                return false;
        }
        else
            return true;
    }

    // verifying entered designation is not smaller than its subordinate designation
    public boolean isGreaterThanChilds(Integer eid,String desg)
    {
        float selfLevel=degRepo.findByDesgName(desg).getLevel();
        List<Employee> list=empRepo.findAllByParentIdOrderByDesignation_levelAsc(eid);
        if(list.size()>0)
        {
            float childLevel=list.get(0).getDesignation().getLevel();
            if(selfLevel<childLevel)
                return true;
            else
                return false;
        }
        else
        {
            return true;
        }
    }

    //verifying entered designation is greater than current designation of employee
    public boolean isGreaterThanCurrentDesignation(Integer eid,String desg)
    {
        Employee employee=empRepo.findByEmpId(eid);
        float selfLevel=employee.getDesignation().getLevel();
        float parentLevel=degRepo.findByDesgName(desg).getLevel();
        if(selfLevel>parentLevel)
            return true;
        else
            return false;
    }

    // Verifying any user exists in database or not for enter Id
    public boolean userExists(Integer eid)
    {
        Employee emp=empRepo.findByEmpId(eid);
        if(emp!=null) {
            return true;
        }
        else {
            return false;
        }
    }

    // Verifying is there any record in database exists for enter query
    public boolean hasData(List<Employee> list)
    {
        if(list.size()>0)
            return true;
        else
            return false;
    }

    // Verifying does entered designation exists in database
    public boolean isDesignationValid(String desg)
    {
        Designation designation=degRepo.findByDesgName(desg);
        if(designation!=null)
            return true;
        else
            return false;
    }

    // Verifying does name is empty or having permitted Regression
    public boolean isValidName(String name)
    {
        if(name!=null)
        {
            if(name.trim().equals(""))
            {
                return false;
            }
            else if(name.matches(".*\\d.*"))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    //Verying does entered id is not have any invalid Id like negative values
    public boolean isValidId(Integer id)
    {
        if(id.intValue()<0)
        {
            return false;
        }
        else
        return true;
    }

    //Verifying either enter data is empty or not
    public boolean haveData(String empName,String desg,Integer parentId)
    {
        if(empName==null && desg==null && parentId==null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
