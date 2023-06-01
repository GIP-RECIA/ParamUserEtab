package com.example.web.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dao.IUserDao;
import com.example.model.Person;
import com.example.model.Structure;
import com.example.service.IStructureService;
import com.example.service.IUserInfoService;

@RestController
//@RequestMapping("/parametab")
public class PersonController {

	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IStructureService structureService;

	@Autowired
	private IUserDao userDao;
	
	/**
	 * GET /etablissments -> Get all the etablissement of a person 
	 */

	@GetMapping("/")
	public ResponseEntity<List<Person>> getEtabs() {
		return new ResponseEntity<>(userInfoService.getAllEtablissement(),  HttpStatus.OK);
	}
	

	/**
	 * GET /get-userinfo -> Get list of isMemberOf in person 
	 */
	@GetMapping("/get-userinfo")
	public ResponseEntity<List<Person>> getUserLdap() {
		return new ResponseEntity<>(userDao.getAllUsersInfo(),  HttpStatus.OK);
	}

	/**
	 * Get /change/{id} -> Change the info of an etablissment using siren
	 */
	@GetMapping(value = "/change/{id}", produces = "application/json")
	public ResponseEntity<? extends Structure> retrieveStructbInJson(@PathVariable("id") final String id,
			HttpServletRequest request) {
		if (id != null)
			return new ResponseEntity<>(structureService.retrieveStructureById(id), HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}



}
