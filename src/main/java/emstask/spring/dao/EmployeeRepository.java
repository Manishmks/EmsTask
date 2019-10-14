package emstask.spring.dao;

import emstask.spring.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Integer>
{
    public List<Employee> findAllByOrderByDesignation_levelAscEmpNameAsc();
    public Employee findByEmpId(int id);
    public List<Employee> findAllByParentIdAndEmpIdIsNotOrderByDesignation_levelAscEmpNameAsc(int parentId,Integer empName);
    public List<Employee> findAllByParentId(Integer eid);
    public Employee findByParentId(Integer parentId);
    public List<Employee> findAllByParentIdOrderByDesignation_levelAsc(Integer pid);
}
