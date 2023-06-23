package fr.recia.paramuseretab.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.recia.paramuseretab.ParametabProjectApplication;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.web.DTOStructure;
import fr.recia.paramuseretab.web.Structure2DTOStructureImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ParametabProjectApplication.class)
@WebMvcTest(ParametabController.class)
@WebAppConfiguration
public class ParametabControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private IStructureService structureService;

    @Mock
    private Structure2DTOStructureImpl structure2dtoStructure;

    @InjectMocks
    private ParametabController parametabController;

    private Structure struct;
    private Map<String, List<String>> otherAttrs = new HashMap<>();
        
    private static ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {

        /**
         * TO DO: 
         * Initialize an etablissement ?? 
         * 
         */
        
        otherAttrs.put("ESCOStructureLogo", Arrays.asList("image0.png"));
        otherAttrs.put("ENTStructureSiteWeb", Arrays.asList("etab01aze.com"));
        struct = new Structure("ABCD123", "Etablissement A1", "Etab A1" , "Etab public", otherAttrs);
        mvc = MockMvcBuilders.standaloneSetup(parametabController).build();
    }


    /**
     * Test : updateForm -> DisplayName et SiteWeb 
     * TO DO :
     * S'inspirer sur le project changeEtablissement pour mocker : StructureRestV2ControllerTest
     */
    @Test
    public void testUpdateForm() throws Exception {
        log.info("*** Test update form ***");
        System.out.println("struct id: " + struct.getId());


        DTOStructure updateForm = new DTOStructure();
        updateForm.setId(struct.getId());
        updateForm.setName(struct.getName());
        updateForm.setDisplayName(struct.getDisplayName());
        updateForm.setDescription(struct.getDescription());
        updateForm.setOtherAttributes(struct.getOtherAttributes());
        updateForm.setStructCustomDisplayName("EX etab A1");
        updateForm.setStructSiteWeb("etab.com");


        //Mockito.when(structureDao.findOneStructureById(dtoStructure.getId())).thenReturn(dtoStructure);
  
        when(structureService.retrieveStructureById(struct.getId())).thenReturn(struct);
        when(structure2dtoStructure.toDTO(struct)).thenReturn(updateForm);

        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.put("/parametab/updateV2/" + struct.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(mapper.writeValueAsString(updateForm));
    
        String uri = "/parametab/updateV2/" + struct.getId();
        String cxt = mapper.writeValueAsString(updateForm);
        System.out.println("out uri : " + uri);
        System.out.println("cxt : " +cxt);

        log.info("mockReq : ", mockReq);

        mvc.perform(mockReq)
        .andDo(print())
        .andExpect(status().isOk()); 

    }

    /**
     * Test : Get info of an etablissement 
     * @throws Exception
     */
    @Test
    public void testGetInfoEtab() throws Exception {

        when(structureService.retrieveStructureById(struct.getId())).thenReturn(struct);
        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.get("/parametab/" + struct.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content(mapper.writeValueAsString(struct));
        mvc.perform(mockReq)
            .andExpect(status().isOk())
            .andDo(print());

    }

    @Test
    public void testUpdateLogo() throws Exception {

        log.info("*** Test update logo ***");

        // Do like testUpdateForm : define DTOStructure, do the setter 
        /**
         * DTOStructure updateLogo = new DTOStructure();
         *  updateLogo.setId(struct.getId());
            updateLogo.setName(struct.getName());
            updateLogo.setDisplayName(struct.getDisplayName());
            updateLogo.setDescription(struct.getDescription());
            updateLogo.setOtherAttributes(struct.getOtherAttributes());
            updateLogo.setStructLogo("path/image");
         */


    }

}
