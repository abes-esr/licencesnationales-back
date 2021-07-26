package fr.abes.licencesnationales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = LicencesNationalesAPIApplication.class)
public class LicencesNationalesAPIApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

}
