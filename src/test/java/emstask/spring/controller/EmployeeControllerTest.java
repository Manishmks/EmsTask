package emstask.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import emstask.spring.Application;
import emstask.spring.model.EmployeePost;
import emstask.spring.model.EmployeePut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class EmployeeControllerTest extends AbstractTransactionalTestNGSpringContextTests
{
    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @BeforeMethod
    public void setUp()
    {
        mvc= MockMvcBuilders.webAppContextSetup(context).build();
    }

    //*******************************************************Test for get All*******************************************************************
    @Test(priority = 1)
    public void getAllTest() throws Exception
    {
        MvcResult result=mvc.perform(MockMvcRequestBuilders.get("/rest/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        String jsonOutput=result.getResponse().getContentAsString();
        int length= JsonPath.parse(jsonOutput).read("$.length()");
        Assert.assertTrue(length>0);

    }

    //*******************************************************Test for get Specific*******************************************************************
    @Test(priority = 2)
    public void getUserTest() throws Exception
    {
        MvcResult result=mvc.perform(MockMvcRequestBuilders.get("/rest/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        String jsonOutput=result.getResponse().getContentAsString();
        int length= JsonPath.parse(jsonOutput).read("$.length()");
        Assert.assertTrue(length>0);

    }

    @Test(priority = 3)
    public void getUserTestInvalidParent() throws Exception
    {
        MvcResult result=mvc.perform(MockMvcRequestBuilders.get("/rest/employees/11"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test(priority = 4)
    public void getUserTestNullParent() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/rest/employees/null"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    //*******************************************************Test for Put Replace*******************************************************************
    @Test
    public void putEmpWithNoData() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("","",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void putEmpWithInvalidPartialData() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void putEmpWithDemotion() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Intern",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void putEmpWithDemotionPossible() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Lead",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void putEmpWithPromotionPossible() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Manager",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void putEmpWithPromotion() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Director",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void replaceWithDirector() throws Exception
    {
        EmployeePut employeePut=new EmployeePut("Captain Marvel","Director",null,true);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employeePut);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    //*******************************************************Test for Post*******************************************************************

    @Test(priority = 0)
    public void createEmployeeTest() throws Exception                //post is working or not
    {
        EmployeePost employeePost=new EmployeePost("wonder woman","Intern",2);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        MvcResult result=mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
    }

    @Test(priority = 1)
    public void directorValidationForManager() throws Exception                //Assigning director with manager
    {
        EmployeePost employeePost=new EmployeePost("wonder woman","Director",2);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest//employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void multipleDirector() throws Exception                             //Adding second Director
    {
        EmployeePost employeePost=new EmployeePost("wonder woman","Director",null);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void noData () throws Exception                //Adding employee with no data
    {
        EmployeePost employeePost=new EmployeePost(null,null,null);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void partialData () throws Exception                //Adding employee with partial data
    {
        EmployeePost employeePost=new EmployeePost("wonder woman","", 2);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void invalidParentId () throws Exception                //Adding employee with non existing manager
    {
        EmployeePost employeePost=new EmployeePost("wonder woman", "Lead",12);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void hierarchyViolation () throws Exception                //Adding employee with violating organisation hierarchy
    {
        EmployeePost employeePost=new EmployeePost("wonder woman", "Lead",8);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        employeePost.setJobTitle("Manager");
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(mapper.writeValueAsString(employeePost)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void invalidDesignation () throws Exception                //Adding employee with non existing Designation
    {
        EmployeePost employeePost=new EmployeePost("wonder woman", "Laead",12);
        ObjectMapper mapper=new ObjectMapper();
        String jsonInput=mapper.writeValueAsString(employeePost);
        mvc.perform(MockMvcRequestBuilders.post("/rest/employees").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    //*************************************************************** Delete Employee Test **************************************************************

    @Test
    public void delInvalidId () throws Exception                //Deleting non existing employee
    {
        mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/121"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    public void delDirectorWithChild () throws Exception                //Deleting director with children
    {
        mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void delDirectorWithoutChild () throws Exception                //Deleting director with children
    {

        mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void delEmployee() throws Exception                //Deleting director without children
    {
        for(int i=10;i>0;i--)
        {
            mvc.perform(MockMvcRequestBuilders.delete("/rest/employees/"+i))
                    .andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
        }

    }

    //****************************************************** Put Update Employee *****************************************

    @Test
    public void updateEmpInvalidId() throws Exception
    {
        EmployeePut employee = new EmployeePut("Rajat","Manager",2,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/13").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpNoData() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,null,null,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpInvalidParId() throws Exception
    {
        EmployeePut employee = new EmployeePut("Mohit","Lead",12343,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpPromotion() throws Exception
    {
        EmployeePut employee = new EmployeePut("Mohit","Director",1,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpDemotion() throws Exception
    {
        EmployeePut employee = new EmployeePut("Mohit","Lead",1,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/2").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void updateEmpDemoteDirector() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,"Lead",null,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpDirectorName() throws Exception
    {
        EmployeePut employee = new EmployeePut("Rajat",null,null,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void updateEmpDirectorWithDirector() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,"Director",null,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
    @Test
    public void updateEmpDirectorWithOutDirector() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,"Manager",null,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void updateEmpDirectorParChange() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,null,2,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/1").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }
    @Test
    public void hulkChildOfCaptain() throws Exception
    {
        EmployeePut employee = new EmployeePut(null,null,4,false);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(employee);
        mvc.perform(MockMvcRequestBuilders.put("/rest/employees/3").content(jsonInput).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

}