package emstask.spring.controller;

import emstask.spring.Application;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class EmployeeControllerTest extends AbstractTestNGSpringContextTests
{
    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @BeforeMethod
    public void setUp()
    {
        mvc= MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGet() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders.get("/rest/employees")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10)));
    }
}