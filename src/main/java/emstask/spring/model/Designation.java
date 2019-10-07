package emstask.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Designation
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    int desId;
    String desgName;
    @JsonIgnore
    float level;

    public int getDesId()
    {
        return desId;
    }

    public void setDesId(int desId) {
        this.desId = desId;
    }

    public String getDesgName() {
        return desgName;
    }

    public void setDesgName(String desgName) {
        this.desgName = desgName.toLowerCase();
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }
}
