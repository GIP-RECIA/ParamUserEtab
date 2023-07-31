package fr.recia.paramuseretab.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.service.IUserInfoService;
import fr.recia.paramuseretab.service.IUserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rest/change")
public class ChangeStructureRestController {
    
    @Autowired
    private IUserService userService;

	@Autowired
	private IStructureService structureService;

    @Autowired
	private IUserInfoService userInfoService;


    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> changeEtab(@PathVariable("id") final String id) {
        try {
            String userID = userInfoService.getUserID();
			if ( id != null) {
				Structure structure = structureService.retrieveStructureById(id);
				if (structure == null) {

					log.info("struct is null");
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}

                // userId, StructID 
				userService.changeCurrentStructure(userID, structure);
				//structureService.invalidateStructureById(id); // refresh cache 
				return new ResponseEntity<>(HttpStatus.OK);
			}
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.getMessage();
            log.info("error change etab : ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
