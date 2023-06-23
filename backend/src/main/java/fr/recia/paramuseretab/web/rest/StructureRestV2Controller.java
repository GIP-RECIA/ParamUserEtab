/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.paramuseretab.web.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.web.DTOStructure;
import fr.recia.paramuseretab.web.Structure2DTOStructureImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by jgribonvald on 27/03/17.
 */
@Slf4j
@RestController
//@RequestMapping(value = "/rest/v2/structures")
public class StructureRestV2Controller {

	@Autowired
	private IStructureService structureService;

	@Autowired
	private IStructureDao structureDao;

	@Autowired
	private Structure2DTOStructureImpl structure2DTOStructure;

	/*
	 * Return always Json data (Accept Http Header value has no impact)
	 * example of call : /CONTEXT-PATH/rest/v2/structures/struct/SIREN
	 */
	@GetMapping(value = "/struct/{id}", produces = "application/json")
	public ResponseEntity<? extends Structure> retrieveStructbInJson(@PathVariable("id") final String id,
			HttpServletRequest request) {
		if (id != null)
			return new ResponseEntity<>(structureService.retrieveStructureById(id), HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	/*
	 * Return always Json data (Accept Http Header value has no impact)
	 * example of call : /CONTEXT-PATH/rest/v2/structures/structs/?ids=SIREN1,SIREN2
	 */
	@GetMapping(value = "/structs/", produces = "application/json")
	public ResponseEntity<Map<String, ? extends Structure>> retrieveStructsInJson(
			@RequestParam("ids") final List<String> ids, HttpServletRequest request) {
		if (ids != null && !ids.isEmpty()) {
			return new ResponseEntity<>(structureService.retrieveStructuresByIds(ids),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	/*
	 * Return always Json data (Accept Http Header value has no impact)
	 * example of call : /CONTEXT-PATH/rest/v2/structures/refresh/SIREN
	 */

	@GetMapping(value = "/refresh/{id}", produces = "application/json")
	public ResponseEntity<Void> refresh(@PathVariable("id") final String id, HttpServletRequest request) {
		if (id != null) {
			structureService.invalidateStructureById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/structures")
	public ResponseEntity<Collection<? extends Structure>> index() {

		return new ResponseEntity<>(structureDao.findAllStructures(), HttpStatus.OK);
	}

	
	/*
	@GetMapping("/parametab/{id}")
	public ResponseEntity<DTOStructure> convertToDTO(@PathVariable("id") final String id) {
	
		try {
			Structure structure = structureDao.findOneStructureById(id);
			if (structure == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			DTOStructure dtoStructure = structure2DTOStructure.toDTO(structure);
			return new ResponseEntity<>(dtoStructure, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error get etab : ", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@PutMapping("/update")
	public ResponseEntity<Void> update(@RequestBody DTOStructure structDto) {

		try {
			if (structDto != null) {
				log.info("struct id: " + structDto.getId());
				log.info("struct dn: " + structDto.getStructCustomDisplayName());
				log.info("struct logo: " + structDto.getStructLogo());
				log.info("struct siteweb: " + structDto.getStructSiteWeb());
				structureDao.saveStructure(structDto);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.getMessage();
			log.info("error update : ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@PutMapping("/updateLogo")
	public ResponseEntity<Void> updateLogo(@RequestBody DTOStructure structDto) {

		try {
			if (structDto != null) {
				log.info("struct id: " + structDto.getId());
				log.info("struct dn: " + structDto.getStructCustomDisplayName());
				log.info("struct logo: " + structDto.getStructLogo());
				structureDao.saveStructure(structDto);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.getMessage();
			log.info("error update : ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}


	@GetMapping("/dto")
	public ResponseEntity<DTOStructure> dto(final DTOStructure structure) {
	
		try {
			DTOStructure dtoS = structure;
			if (dtoS == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(dtoS, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	*/
}
