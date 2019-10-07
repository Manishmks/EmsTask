package emstask.spring.service;

import emstask.spring.dao.DesignationRepository;
import emstask.spring.dao.EmployeeRepository;
import emstask.spring.model.Designation;
import emstask.spring.model.Employee;
import emstask.spring.model.EmployeePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class EmployeeService
{
    @Autowired
    EmployeeRepository empRepo;
    @Autowired
    DesignationRepository degRepo;


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

    public Map<String, Object> getUserDetails(Integer eid)
    {
        Employee manager=null;
        List<Employee> colleagues=null;
        Map<String,Object> map=new LinkedHashMap<>();

        boolean userExists=userExists(eid);
            Employee emp=empRepo.findByEmpId(eid);

            map.put("Employee",emp);

            if(emp.getParentId()!=null) {
                manager = empRepo.findByEmpId(emp.getParentId());
                map.put("Manager",manager);

                colleagues=empRepo.findAllByParentIdAndEmpIdIsNot(emp.getParentId(),emp.getEmpId());
                map.put("Colleagues",colleagues);
            }

            List<Employee> reporting=empRepo.findAllByParentIdAndEmpIdIsNot(emp.getEmpId(),emp.getEmpId());
            if(reporting.size()!=0)
                map.put("Reporting To",reporting);

            return map;
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
                        return new ResponseEntity("Director having childs cannot be deleted",HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity("Employee Does't Exists",HttpStatus.BAD_REQUEST);
            }
        }

    public ResponseEntity addUser(EmployeePost employee)
    {
        String empName=employee.getEmpName();
        String desg=employee.getEmpDesg();
        Integer parentId=employee.getParentId();

        if(parentId==null) {
            Employee director = empRepo.findByParentId(null);
            if (director != null) {
                return new ResponseEntity("Director Already Exists ParentId cannot be NULL", HttpStatus.BAD_REQUEST);
            }
            else
            {
                if(desg.equals("director"))
                {
                    Designation designation=degRepo.findByDesgName(desg);
                    Employee emp=new Employee(designation,parentId,empName);
                    empRepo.save(emp);
                    return new ResponseEntity("Employee Created",HttpStatus.CREATED);
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
                    return new ResponseEntity("Employee Created",HttpStatus.CREATED);
                }
                else
                {
                    return new ResponseEntity(desg+" cannot be child of "+parentRecord.getDesgName(),HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    public ResponseEntity updateUser(int eid,EmployeePost emp)
    {
        if(userExists(eid))
        {
            if(emp.isReplace())
            {
                Integer parent=null;
                Employee employee=empRepo.findByEmpId(eid);
                    float oldLevel=employee.getDesignation().getLevel();
                    float currLevel=degRepo.findByDesgName(emp.getEmpDesg()).getLevel();
                    if(oldLevel>=currLevel)
                    {
                        parent=employee.getParentId();
                        empRepo.delete(employee);
                        Employee tempEmployee=new Employee(degRepo.findByDesgName(emp.getEmpDesg()),parent,emp.getEmpName());
                       /* tempEmployee.designation=degRepo.findByDesgName(emp.getEmpDesg());
                        tempEmployee.setEmpName(emp.getEmpName());
                        tempEmployee.setParentId(parent);
                        empRepo.save(tempEmployee);*/
                        List<Employee> list=empRepo.findAllByParentId(eid);
                        for(Employee empTemp:list)
                        {
                            empTemp.setParentId(tempEmployee.getEmpId());
                            empRepo.save(empTemp);
                        }
                        return new ResponseEntity("User Replaced",HttpStatus.OK);
                    }
                    else
                    {
                        return new ResponseEntity("Bad Request",HttpStatus.BAD_REQUEST);
                    }
            }
            else
            {
                //false
            }
        }
        else
        {

        }

        return null;
    }
}
