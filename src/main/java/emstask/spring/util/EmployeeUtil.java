package emstask.spring.util;

import emstask.spring.dao.DesignationRepository;
import emstask.spring.dao.EmployeeRepository;
import emstask.spring.model.Designation;
import emstask.spring.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EmployeeUtil
{
    @Autowired
    EmployeeRepository empRepo;
    @Autowired
    DesignationRepository degRepo;

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

    public boolean hasData(List<Employee> list)
    {
        if(list.size()>0)
            return true;
        else
            return false;
    }

    public boolean isDesignationValid(String desg)
    {
        Designation designation=degRepo.findByDesgName(desg);
        if(designation!=null)
            return true;
        else
            return false;
    }

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

    public boolean isValidId(Integer id)
    {
        if(id.intValue()<0)
        {
            return false;
        }
        else
        return true;
    }

}
