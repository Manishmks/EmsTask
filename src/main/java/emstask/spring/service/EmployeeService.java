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

    //Get details of user by Employee ID
    public ResponseEntity getUserDetails(Integer eid)
    {
        Employee manager;
        List<Employee> colleagues;
        Map<String, Object> result = new LinkedHashMap<>();
        boolean userExists = false;
        if (eid != null && eid > 0) {
            userExists = userExists(eid);
        } else if (eid < 0) {
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_ID"),HttpStatus.BAD_REQUEST);
        }

            if (userExists) // Getting details of user if entered emp Id exists in database
            {
                //Adding employee profile to result
                Employee emp = empRepo.findByEmpId(eid);
                result.put("id", emp.getEmpId());
                result.put("name", emp.getEmpName());
                result.put("jobTitle", emp.getDesgName());
                result.put("Employee", emp);

                //Adding employee manager if exists to result in sorted order with Employee designation and Name
                if (emp.getParentId() != null) {
                    manager = empRepo.findByEmpId(emp.getParentId());
                    result.put("manager", manager);
                    colleagues = empRepo.findAllByParentIdAndEmpIdIsNotOrderByDesignation_levelAscEmpNameAsc(emp.getParentId(), emp.getEmpId());
                    result.put("colleagues", colleagues);
                }

                //Adding Subordinates to result in sorted order with Employee designation and Name
                List<Employee> reporting = empRepo.findAllByParentIdAndEmpIdIsNotOrderByDesignation_levelAscEmpNameAsc(emp.getEmpId(), emp.getEmpId());
                if (reporting.size() != 0)
                    result.put("subordinates", reporting);

            return new ResponseEntity<>(result, HttpStatus.OK);
            }
            else // If enter User ID does't exists in database
            {
                return new ResponseEntity<>(messageUtil.getMessage("EMP_NOT_EXISTS"),HttpStatus.NOT_FOUND);
            }
    }

    // Get List of all employees in database Sorted by deisgnation then Employee name
    public ResponseEntity getAll()
    {
            List<Employee> result=empRepo.findAllByOrderByDesignation_levelAscEmpNameAsc();
            if(hasData(result))
                return new ResponseEntity<>(result, HttpStatus.OK);
            else
                return new ResponseEntity<>(messageUtil.getMessage("NO_DATA_FOUND"),HttpStatus.BAD_REQUEST);
    }

    // Delete an employee with employee ID
    public ResponseEntity deleteUser(Integer eid)
    {
        // Check UserId consist valid regression
        if(!isValidId(eid))
        {
            return new ResponseEntity("INVALID_ID",HttpStatus.BAD_REQUEST);
        }

        //Check User exists in database or not
            boolean userExists=userExists(eid);
            if(userExists)
            {
                // If entered designation is director
                Employee emp=empRepo.findByEmpId(eid);
                if(emp.getDesgName().equals("Director"))
                {
                    //Getting all employee reporting to entered EmpId
                    List<Employee> list=empRepo.findAllByParentId(emp.getEmpId());
                    if(hasData(list))
                    {
                        // Not able to delete if director having someone reporting to
                        return new ResponseEntity<>(messageUtil.getMessage("UNABLE_TO_DELETE_DIRECTOR"),HttpStatus.BAD_REQUEST);
                    }
                    else
                    {
                        //Able to delete as director have no child
                        empRepo.delete(emp);
                        return new ResponseEntity<>(messageUtil.getMessage("DELETED"),HttpStatus.NO_CONTENT);
                    }
                }
                else // If entered designation is not director
                {
                    int parentId=emp.getParentId();
                    // Moving children of employee to its parent
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
            else // No employee exists with entered empId
            {
                return new ResponseEntity<>(messageUtil.getMessage("EMP_NOT_EXISTS"),HttpStatus.NOT_FOUND);
            }
        }

     //Add New User to database
    public ResponseEntity addUser(EmployeePost employee)
    {
        String empName=employee.getName();
        String desg=employee.getJobTitle();
        Integer parentId=employee.getManagerId();

        // checking is entered parent is Valid or Not
        if(parentId!=null)
        {
            if(!isValidId(parentId))
            {
                parentId=null;
            }
        }

        //Checking entered body having data or not
        if(!haveData(empName,desg,parentId))
        {
            return new ResponseEntity<>(messageUtil.getMessage("INSUFFICIENT_DATA"),HttpStatus.BAD_REQUEST);
        }

        // Validating Entered Designation
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
        // Validating entered name of user
        if(!isValidName(empName))
        {
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_EMP_NAME"), HttpStatus.BAD_REQUEST);
        }

        if(parentId==null ) {
            Employee director = empRepo.findByParentId(null);
            //checking either director already exists
            if (director != null) {
                return new ResponseEntity<>(messageUtil.getMessage("DIRECTOR_EXISTS"), HttpStatus.BAD_REQUEST);
            }
            else // if director does not exists and entered designation is director
            {
                if(desg.equals("Director"))
                {
                    Designation designation=degRepo.findByDesgName(desg);
                    Employee emp=new Employee(designation,parentId,empName);
                    empRepo.save(emp);
                    return new ResponseEntity<>(emp,HttpStatus.CREATED);
                }
                else // if no director exists then add director first before adding any other employee
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
            else // Verifying that entered designation must me smaller than its manager's designation
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
                }
            }
        }
    }

    //Updating profile of an existing user in database
    public ResponseEntity updateUser(int eid, EmployeePut emp)
    {
        //Validating entered userId
        if(!isValidId(eid))
        {
            return new ResponseEntity("INVALID_ID",HttpStatus.BAD_REQUEST);
        }
        // Verifying does entered body is empty
        if(!haveData(emp.getName(),emp.getJobTitle(),emp.getManagerId()))
        {
            return new ResponseEntity("NO DATA",HttpStatus.BAD_REQUEST);
        }
        //Validating ManagerId
        if(emp.getManagerId()!=null)
        {
            if(!isValidId(emp.getManagerId()))
            {
                return new ResponseEntity("INVALID_MANAGER",HttpStatus.BAD_REQUEST);
            }
        }
        // Verifying user exists or not in database
        if(userExists(eid)) //if exists
        {
            String userDesignation;
            if(emp.isReplace()) // if Replace parameter is true
            {
                userDesignation=emp.getJobTitle();
                // Validating Designation
                if(userDesignation==null) {
                    return new ResponseEntity<>(messageUtil.getMessage("NULL_DESIGNATION"), HttpStatus.BAD_REQUEST);
                }
                else
                {
                    if (!isDesignationValid(userDesignation))
                        return new ResponseEntity<>(messageUtil.getMessage("INVALID_DESIGNATION"),HttpStatus.BAD_REQUEST);
                }
                // Validating name of user
                if(!isValidName(emp.getName()))
                {
                    return new ResponseEntity<>(messageUtil.getMessage("INVALID_EMP_NAME"), HttpStatus.BAD_REQUEST);
                }

                // Verifying that designation of user must me smaller than its Manager and greater than his child
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
                    }
                    else
                        return new ResponseEntity<>(messageUtil.getMessage("INVALID_PARENT"),HttpStatus.BAD_REQUEST);
            }
            else // if Replace parameter is false
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