package emstask.spring.dao;

import emstask.spring.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation,Integer>
{
    public Designation findByDesgName(String desName);
}
