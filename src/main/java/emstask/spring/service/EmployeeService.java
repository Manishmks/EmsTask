package emstask.spring.service;

import emstask.spring.model.*;
import emstask.spring.util.EmployeeUtil;
import emstask.spring.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service
public class EmployeeService extends EmployeeUtil
{
    @Autowired
    MessageUtil messageUtil;

    public ResponseEntity getUserDetails(Integer eid)
    {
        Employee manager;
        List<Employee> colleagues;
        Map<String, Object> map = new LinkedHashMap<>();
        boolean userExists = false;
        if (eid != null && eid > 0) {
            userExists = userExists(eid);
        } else if (eid < 0) {
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_ID"),HttpStatus.BAD_REQUEST);
        }
            if (userExists) {
                Employee emp = empRepo.findByEmpId(eid);
                map.put("id", emp.getEmpId());
                map.put("name", emp.getEmpName());
                map.put("jobTitle", emp.getDesgName());
            map.put("Employee", emp);

                if (emp.getParentId() != null) {
                    manager = empRepo.findByEmpId(emp.getParentId());
                    map.put("manager", manager);
                    colleagues = empRepo.findAllByParentIdAndEmpIdIsNotOrderByDesignation_levelAscEmpNameAsc(emp.getParentId(), emp.getEmpId());
                    map.put("colleagues", colleagues);
                }

                List<Employee> reporting = empRepo.findAllByParentIdAndEmpIdIsNotOrderByDesignation_levelAscEmpNameAsc(emp.getEmpId(), emp.getEmpId());
                if (reporting.size() != 0)
                    map.put("subordinates", reporting);

            return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(messageUtil.getMessage("EMP_NOT_EXISTS"),HttpStatus.NOT_FOUND);
            }
    }

    public ResponseEntity getAll()
    {
            List<Employee> list=empRepo.findAllByOrderByDesignation_levelAscEmpNameAsc();
            if(hasData(list))
                return new ResponseEntity<>(list, HttpStatus.OK);
            else
                return new ResponseEntity<>(messageUtil.getMessage("NO_DATA_FOUND"),HttpStatus.BAD_REQUEST);
        }

    public ResponseEntity deleteUser(Integer eid)
    {
        if(eid<0)
        {
            return new ResponseEntity("INVALID_ID",HttpStatus.BAD_REQUEST);
        }

            boolean userExists=userExists(eid);
            if(userExists)
            {
                Employee emp=empRepo.findByEmpId(eid);
                if(emp.getDesgName().equals("Director"))
                {
                    List<Employee> list=empRepo.findAllByParentId(emp.getEmpId());
                    if(hasData(list))
                    {
                        // Not able to delete
                        return new ResponseEntity<>(messageUtil.getMessage("UNABLE_TO_DELETE_DIRECTOR"),HttpStatus.BAD_REQUEST);
                    }
                    else
                    {
                        //Able to delete
                        empRepo.delete(emp);
                        return new ResponseEntity<>(messageUtil.getMessage("DELETED"),HttpStatus.NO_CONTENT);
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
                    return new ResponseEntity<>(messageUtil.getMessage("DELETED"),HttpStatus.NO_CONTENT);
                }
            }
            else
            {
                return new ResponseEntity<>(messageUtil.getMessage("EMP_NOT_EXISTS"),HttpStatus.NOT_FOUND);
            }
        }

    public ResponseEntity addUser(EmployeePost employee)
    {
        String empName=employee.getName();
        String desg=employee.getJobTitle();
        Integer parentId=employee.getManagerId();
        if(parentId!=null)
        {
            if(parentId.intValue()<0)
            {
                parentId=null;
            }
        }

        if(empName==null && desg==null && parentId==null)
        {
            return new ResponseEntity<>(messageUtil.getMessage("INSUFFICIENT_DATA"),HttpStatus.BAD_REQUEST);
        }

        if(desg!=null)
        {
            if(!isDesignationValid(desg))
            {
                return new ResponseEntity<>(messageUtil.getMessage("INVALID_DESIGNATION"), HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            return new ResponseEntity<>(messageUtil.getMessage("NULL_DESIGNATION"), HttpStatus.BAD_REQUEST);
        }

        if(!isValidName(empName))
        {
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_EMP_NAME"), HttpStatus.BAD_REQUEST);
        }
        if(parentId==null ) {
            Employee director = empRepo.findByParentId(null);
            if (director != null) {
                return new ResponseEntity<>(messageUtil.getMessage("DIRECTOR_EXISTS"), HttpStatus.BAD_REQUEST);
            }
            else
            {
                if(desg.equals("Director"))
                {
                    Designation designation=degRepo.findByDesgName(desg);
                    Employee emp=new Employee(designation,parentId,empName);
                    empRepo.save(emp);
                    return new ResponseEntity<>(emp,HttpStatus.CREATED);
                }
                else
                {
                    return new ResponseEntity<>(messageUtil.getMessage("NO_DIRECTOR_EXISTS"),HttpStatus.BAD_REQUEST);
                }

            }
        }
        else
        {
            Employee parent=empRepo.findByEmpId(parentId);
            if(parent==null)
            {
                return new ResponseEntity<>(messageUtil.getMessage("PARENT_NOT_EXISTS"), HttpStatus.BAD_REQUEST);
            }
            else
            {
                Designation designation=degRepo.findByDesgName(desg);
                float currentLevel=designation.getLevel();

                Employee parentRecord=empRepo.findByEmpId(parentId);
                float parentLevel=parentRecord.getDesignation().getLevel();

                if(parentLevel<currentLevel)
                {
                    Employee emp=new Employee(designation,parentId,empName);
                    empRepo.save(emp);
                    return new ResponseEntity<>(emp,HttpStatus.CREATED);
                }
                else
                {
                    return new ResponseEntity<>(messageUtil.getMessage("INVALID_PARENT"),HttpStatus.BAD_REQUEST);
                    //desg+" cannot be child of "+parentRecord.getDesgName()
                }
            }
        }
    }

    public ResponseEntity updateUser(int eid, EmployeePut emp)
    {
        if(eid<0)
        {
            return new ResponseEntity("INVALID_ID",HttpStatus.BAD_REQUEST);
        }
        if(emp.getName()==null && emp.getManagerId()==null && emp.getJobTitle()==null)
        {
            return new ResponseEntity("NO DATA",HttpStatus.BAD_REQUEST);
        }
        if(emp.getManagerId()!=null)
        {
            if(!isValidId(emp.getManagerId()))
            {
                return new ResponseEntity("INVALID_MANAGER",HttpStatus.BAD_REQUEST);
            }
        }
        if(userExists(eid))
        {
            String userDesignation;

            if(emp.isReplace())
            {
                userDesignation=emp.getJobTitle();
                if(userDesignation==null) {
                    return new ResponseEntity<>(messageUtil.getMessage("NULL_DESIGNATION"), HttpStatus.BAD_REQUEST);
                }
                else
                {
                    if (!isDesignationValid(userDesignation))
                        return new ResponseEntity<>(messageUtil.getMessage("INVALID_DESIGNATION"),HttpStatus.BAD_REQUEST);
                }
                if(!isValidName(emp.getName()))
                {
                    return new ResponseEntity<>(messageUtil.getMessage("INVALID_EMP_NAME"), HttpStatus.BAD_REQUEST);
                }
                    Integer parent=null;
                    Employee employee=empRepo.findByEmpId(eid);
                    if(isGreaterThanChilds(eid,userDesignation) && isSmallerThanParent(eid,userDesignation))
                    {
                        parent=employee.getParentId();
                        empRepo.delete(employee);
                        Employee tempEmployee=new Employee(degRepo.findByDesgName(userDesignation),parent,emp.getName());
                        empRepo.save(tempEmployee);
                        List<Employee> list=empRepo.findAllByParentId(eid);
                        for(Employee empTemp:list)
                        {
                            empTemp.setParentId(tempEmployee.getEmpId());
                            empRepo.save(empTemp);
                        }
                        return getUserDetails(tempEmployee.getEmpId());
//                        return new ResponseEntity<>(tempEmployee,HttpStatus.OK);
//                      return new ResponseEntity<>(getUserDetails(tempEmployee.getEmpId()),HttpStatus.OK);

                    }
                    else
                        return new ResponseEntity<>(messageUtil.getMessage("INVALID_PARENT"),HttpStatus.BAD_REQUEST);
            }
            else
            {
                userDesignation=emp.getJobTitle();
                Employee employee=empRepo.findByEmpId(eid);
                Integer parentId=emp.getManagerId();

                if(userDesignation!=null)
                {
                    if (!isDesignationValid(userDesignation))
                        return new ResponseEntity<>(messageUtil.getMessage("INVALID_DESIGNATION"),HttpStatus.BAD_REQUEST);
                    else
                    {
                        if(isGreaterThanChilds(eid,userDesignation) && isSmallerThanParent(eid,userDesignation))
                        {
                            employee.setDesignation(degRepo.findByDesgName(userDesignation));
                        }
                        else
                        {
                            return new ResponseEntity<>(messageUtil.getMessage("INVALID_PARENT"),HttpStatus.BAD_REQUEST);
                        }
                    }
                }
                if(emp.getName()!=null)
                {
                    if(emp.getName().trim().equals(""))
                    {
                        return new ResponseEntity<>(messageUtil.getMessage("BLANK_NAME"),HttpStatus.BAD_REQUEST);
                    }
                }

                if(parentId!=null)
                {
                    if(!userExists(parentId))
                        return new ResponseEntity<>(messageUtil.getMessage("PARENT_NOT_EXISTS"),HttpStatus.BAD_REQUEST);
                    else {
                        if(isGreaterThanCurrentDesignation(eid,empRepo.findByEmpId(parentId).getDesgName()))
                        {
                            employee.setParentId(parentId);
                        }
                        else
                        {
                            return new ResponseEntity<>(messageUtil.getMessage("INVALID_PARENT"),HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                if(emp.getName()!=null)
                {
                    employee.setEmpName(emp.getName());
                }
                empRepo.save(employee);
//                return new ResponseEntity<>(employee,HttpStatus.OK);
                return getUserDetails(eid);
            }
        }
        //User does't Exists
        else
        {
            return new ResponseEntity<>(messageUtil.getMessage("EMP_NOT_EXISTS"),HttpStatus.BAD_REQUEST);
        }

    }
}