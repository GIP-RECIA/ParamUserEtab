package com.example.web.rest;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.ParametabProjectApplication;
import com.example.service.IUserInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ParametabProjectApplication.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    // @MockBean
    // private IUserInfoService userInfoService;

    @Autowired
    private MockMvc restPersonMock;

    @Test
    void testGetEtabs() throws Exception {

        // TEST : get all the etablissements
        restPersonMock.perform(MockMvcRequestBuilders.get("/parametab/"))
                        // .andExpectAll(
                        //     MockMvcResultMatchers.status().isOk(),
                        //     MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        //     MockMvcResultMatchers.jsonPath("$.person.name").value("Jason")
                        // );
                        .andExpect(MockMvcResultMatchers.status().isOk());
        log.debug("get etab ok");
    }


}
