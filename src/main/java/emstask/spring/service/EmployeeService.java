package emstask.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import emstask.spring.ResultMessages;
import emstask.spring.dao.DesignationRepository;
import emstask.spring.dao.EmployeeRepository;
import emstask.spring.model.*;
import emstask.spring.util.MessageUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import static emstask.spring.ResultMessages.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class EmployeeUtil
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
        if(emp!=null)
            return true;
        else
            return false;
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

}

@Service
public class EmployeeService extends EmployeeUtil
{
    @Autowired
    MessageUtil messageUtil;
    public ResponseEntity getUserDetails(Integer eid)
    {
        Employee manager=null;
        List<Employee> colleagues=null;
        Map<String,Object> map=new LinkedHashMap<>();

        boolean userExists;
        if(eid!=null)
        {
            userExists=userExists(eid);
        }
        else
        {
            return new ResponseEntity(INVALID_PARENT,HttpStatus.BAD_REQUEST);
        }
        if(userExists) {
            Employee emp = empRepo.findByEmpId(eid);

            map.put("Employee", emp);

            if (emp.getParentId() != null) {
                manager = empRepo.findByEmpId(emp.getParentId());
                map.put("Manager", manager);

                colleagues = empRepo.findAllByParentIdAndEmpIdIsNot(emp.getParentId(), emp.getEmpId());
                map.put("Colleagues", colleagues);
            }

            List<Employee> reporting = empRepo.findAllByParentIdAndEmpIdIsNot(emp.getEmpId(), emp.getEmpId());
            if (reporting.size() != 0)
                map.put("Reporting To", reporting);

            return new ResponseEntity(map, HttpStatus.OK);
        }
        else
            {
            return new ResponseEntity(PARENT_NOT_EXISTS,HttpStatus.NOT_FOUND);
        }
        }

    public ResponseEntity getAll()
    {
            List<Employee> list=empRepo.findAllByOrderByDesignation_levelAscEmpNameAsc();
            if(hasData(list))
                return new ResponseEntity<>(list, HttpStatus.OK);
            else
                return new ResponseEntity<>("No Records Found",HttpStatus.NOT_FOUND);
        }

    public ResponseEntity deleteUser(Integer eid)
    {
            boolean userExists=userExists(eid);
            if(userExists)
            {
                Employee emp=empRepo.findByEmpId(eid);
                if(emp.getDesgName().equals("director"))
                {
                    List<Employee> list=empRepo.findAllByParentId(emp.getEmpId());
                    if(hasData(list))
                    {
                        // Not able to delete
                        return new ResponseEntity("Director having childs cannot be deleted",HttpStatus.FORBIDDEN);
                    }
                    else
                    {
                        //Able to delete
                        empRepo.delete(emp);
                        return new ResponseEntity("Deleted Successfully",HttpStatus.OK);
                    }
                }
                else
                {
                    int parentId=emp.getParentId();
                    List<Employee> childs=empRepo.findAllByParentId(emp.getEmpId());
                    for(Employee employee:childs)
                    {
                        employee.setParentId(parentId);
                        empRepo.save(employee);
                    }
                    empRepo.delete(emp);
                    return new ResponseEntity("Deleted Successfully",HttpStatus.OK);
                }
            }
            else
            {
                return new ResponseEntity("Employee Does't Exists",HttpStatus.NOT_FOUND);
            }
        }

    public ResponseEntity addUser(EmployeePost employee)
    {
        String empName=employee.getEmpName();
        String desg=employee.getEmpDesg();
        Integer parentId=employee.getParentId();
        if(empName==null && desg==null && parentId==null)
        {
            return new ResponseEntity("No Data",HttpStatus.BAD_REQUEST);
        }

        if(desg!=null)
        {
            if(!isDesignationValid(desg))
            {
                return new ResponseEntity("Please Enter Valid Designation", HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            return new ResponseEntity("Designation cannot be NULL", HttpStatus.BAD_REQUEST);
        }

        if(!isValidName(empName))
        {
            return new ResponseEntity("Please enter a valid name", HttpStatus.BAD_REQUEST);
        }
        if(parentId==null) {
            Employee director = empRepo.findByParentId(null);
            if (director != null) {
                return new ResponseEntity("Director Already Exists ParentId cannot be NULL", HttpStatus.FORBIDDEN);
            }
            else
            {
                if(desg.equals("director"))
                {
                    Designation designation=degRepo.findByDesgName(desg);
                    Employee emp=new Employee(designation,parentId,empName);
                    empRepo.save(emp);
                    return new ResponseEntity(messageUtil.getMessage("msg1","Hello"),HttpStatus.OK);
                }
                else
                {
                    return new ResponseEntity("No Director found! Please Add Director First",HttpStatus.BAD_REQUEST);
                }

            }
        }
        else
        {
            Employee parent=empRepo.findByEmpId(parentId);
            if(parent==null)
            {
                return new ResponseEntity("Parent Does't Exists", HttpStatus.BAD_REQUEST);
            }
            else
            {
                Designation designation=degRepo.findByDesgName(desg);
                System.out.println(desg);
                float currentLevel=designation.getLevel();

                Employee parentRecord=empRepo.findByEmpId(parentId);
                float parentLevel=parentRecord.getDesignation().getLevel();

                if(parentLevel<currentLevel)
                {
                    Employee emp=new Employee(designation,parentId,empName);
                    empRepo.save(emp);
                    return new ResponseEntity(messageUtil.getMessage("msg1","Manish"),HttpStatus.OK);
                }
                else
                {
                    return new ResponseEntity(INVALID_PARENT,HttpStatus.BAD_REQUEST);
                    //desg+" cannot be child of "+parentRecord.getDesgName()
                }
            }
        }
    }

    public ResponseEntity updateUser(int eid, EmployeePut emp) throws Exception {
        //User Exists
        if(userExists(eid))
        {
            String userDesignation;

            if(emp.isReplace())
            {
                userDesignation=emp.getEmpDesg();
                if(userDesignation==null) {
                    return new ResponseEntity("Designation cannot be NULL", HttpStatus.BAD_REQUEST);
                }
                else
                {
                    if (!isDesignationValid(userDesignation))
                        return new ResponseEntity<>("Designation does't exists! Please enter valid designation",HttpStatus.BAD_REQUEST);
                }
                if(!isValidName(emp.getEmpName()))
                {
                    return new ResponseEntity("Please enter a valid name", HttpStatus.BAD_REQUEST);
                }
                    Integer parent=null;
                    Employee employee=empRepo.findByEmpId(eid);
                    if(isGreaterThanChilds(eid,userDesignation) && isSmallerThanParent(eid,userDesignation))
                    {
                        parent=employee.getParentId();
                        empRepo.delete(employee);
                        Employee tempEmployee=new Employee(degRepo.findByDesgName(userDesignation),parent,emp.getEmpName());
                        empRepo.save(tempEmployee);
                        List<Employee> list=empRepo.findAllByParentId(eid);
                        for(Employee empTemp:list)
                        {
                            empTemp.setParentId(tempEmployee.getEmpId());
                            empRepo.save(empTemp);
                        }
                        return new ResponseEntity("User Replaced",HttpStatus.OK);
                    }
                    else
                        return new ResponseEntity(employee.getDesignation().getDesgName()+" cannot be replaced with "+userDesignation,HttpStatus.BAD_REQUEST);
            }
            else
            {
                userDesignation=emp.getEmpDesg();
                Employee employee=empRepo.findByEmpId(eid);
                Integer parentId=emp.getParentId();

                if(userDesignation!=null)
                {
                    if (!isDesignationValid(userDesignation))
                        return new ResponseEntity("Designation does't exists! Please enter valid designation",HttpStatus.BAD_REQUEST);
                    else
                    {
                        if(isGreaterThanChilds(eid,userDesignation) && isSmallerThanParent(eid,userDesignation))
                        {
                            employee.setDesignation(degRepo.findByDesgName(userDesignation));
                        }
                        else
                        {
                            return new ResponseEntity(employee.getDesignation().getDesgName()+" cannot be replaced with "+userDesignation,HttpStatus.FORBIDDEN);
                        }
                    }
                }
                if(emp.getEmpName()!=null)
                {
                    if(emp.getEmpName().trim().equals(""))
                    {
                        return new ResponseEntity("Name Cannot be Blank",HttpStatus.BAD_REQUEST);
                    }
                }

                if(parentId!=null)
                {
                    if(!userExists(parentId))
                        return new ResponseEntity("Parent does't Exists",HttpStatus.BAD_REQUEST);
                    else {
                        if(isGreaterThanCurrentDesignation(eid,empRepo.findByEmpId(parentId).getDesgName()))
                        {
                            employee.setParentId(parentId);
                        }
                        else
                        {
                            return new ResponseEntity("Invalid ParentId",HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                if(emp.getEmpName()!=null)
                {
                    employee.setEmpName(emp.getEmpName());
                }
                empRepo.save(employee);
                return new ResponseEntity("User Updated",HttpStatus.OK);
            }
        }
        //User does't Exists
        else
        {
            return new ResponseEntity("User does't Exists",HttpStatus.NOT_FOUND);
        }

    }
}