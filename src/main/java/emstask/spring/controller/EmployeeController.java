package emstask.spring.controller;
import emstask.spring.dao.DesignationRepository;
import emstask.spring.dao.EmployeeRepository;
import emstask.spring.model.EmployeePost;
import emstask.spring.model.EmployeePut;
import emstask.spring.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
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
       return empService.getUserDetails(aid);
    }

    @PutMapping("/employees/{empId}")
    public ResponseEntity updateEmployee(@PathVariable("empId") int empId,@RequestBody EmployeePut emp) throws Exception {
        return empService.updateUser(empId,emp);
    }

    @DeleteMapping("/employees/{eid}")
    public ResponseEntity deleteEmployee(@PathVariable("eid") int eid)
    {
        return  empService.deleteUser(eid);
    }
}