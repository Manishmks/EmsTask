package emstask.spring.controller;


import emstask.spring.dao.DesignationRepository;
import emstask.spring.dao.EmployeeRepository;
import emstask.spring.model.Designation;
import emstask.spring.model.Employee;
import emstask.spring.model.EmployeePost;
import emstask.spring.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest")
public class EmployeeController
{
    @Autowired
    EmployeeRepository empRepo;
    @Autowired
    DesignationRepository degRepo;
    @Autowired
    EmployeeService empService;

    @GetMapping(path = "/employees",produces = "application/json")
    public ResponseEntity getAllEmployees()
    {
       return empService.getAll();
    }

    @PostMapping(path = "/employees")
    public ResponseEntity createEmployee(@RequestBody EmployeePost employee)
    {
         return empService.addUser(employee);
    }


    @GetMapping("/employees/{aid}")
    public ResponseEntity getEmployee(@PathVariable("aid") Integer aid)
    {
        boolean userExists=empService.userExists(aid);
        if(userExists)
        {
            Map<String,Object> map=empService.getUserDetails(aid);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("Employee does't exists",HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/employees/{empId}")
    public String updateEmployee(@PathVariable("empId") int empId,@RequestBody EmployeePost emp)
    {
        empService.updateUser(empId,emp);
        return null;
    }

    @DeleteMapping("/employees/{eid}")
    public ResponseEntity deleteEmployee(@PathVariable("eid") int eid)
    {
        ResponseEntity entity=empService.deleteUser(eid);
        return entity;
    }
}