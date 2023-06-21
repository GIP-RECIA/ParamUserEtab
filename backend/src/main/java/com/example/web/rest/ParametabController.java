package com.example.web.rest;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.dao.IStructureDao;
import com.example.dao.bean.UploadResponse;
import com.example.model.Person;
import com.example.model.Structure;
import com.example.service.IImageUrlPath;
import com.example.service.ILogoStorageService;
import com.example.service.IStructureService;
import com.example.service.IUserInfoService;
import com.example.service.impl.LogoStorageServiceImpl;
import com.example.web.DTOStructure;
import com.example.web.Structure2DTOStructureImpl;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/parametab")
public class ParametabController {
    

    @Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IStructureService structureService;

	@Autowired
	private Structure2DTOStructureImpl structure2DTOStructure;

    @Autowired 
    private ILogoStorageService logoStorage;

	@Autowired
	private ILogoStorageService logoStorageService;

	@Autowired
	private LogoStorageServiceImpl logoStorageServiceImpl;

	@Autowired
	private IStructureDao structureDao;

	private IImageUrlPath oldUrl;
	private IImageUrlPath newUrl;

	private IImageUrlPath calculOldImageUrlPath(DTOStructure str) {
		IImageUrlPath url = null;
		if (str != null) {
			String link = str.getStructLogo();
			if (link != null) {
				url =  logoStorageService.makeImageUrlPath(link);
			}
		}
		return url;
	}

	private static boolean mkdir (String rep) {
		File file = new File(rep);
		// si le repertoire n'existe pas on le cree.
		return file.mkdirs() || file.isDirectory();
		
	}

	private IImageUrlPath calculNewImageUrlPath(DTOStructure str, IImageUrlPath old) {
		IImageUrlPath url = null;
		if (str != null) {
			log.info("str not null");
			int version = 0;
			if (old != null) {
				log.info("old not null");
				version = Integer.parseInt(old.getVersion());
			}
			log.info("old is null");
			url = logoStorageService.makeImageUrlPath(str.getId(),	 version + 1);
			// if (! mkdir(url.getPathUser())) {
			// 	url = null;
			// }
		}
		return url;
	}
		
	
	/**
	 * GET /etablissments -> Get all the etablissement of a person 
	 */

	@GetMapping("/")
	public ResponseEntity<List<Person>> getEtabs() {
		return new ResponseEntity<>(userInfoService.getAllEtablissement(),  HttpStatus.OK);
	}


    /**
     * Get info of an etablissement 
     */
	@GetMapping("/{id}")
	public ResponseEntity<DTOStructure> convertToDTO(@PathVariable("id") final String id) {
	
		try {
			Structure structure = structureService.retrieveStructureById(id);
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

    /**
     * Update form etablissement 
     */

    @PutMapping("/update")
	public ResponseEntity<Void> update(@RequestBody DTOStructure structDto) {

		try {
			if (structDto != null && structDto.getId() != null) {
				//structureService.updateStructure(structDto);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.getMessage();
			log.info("error update : ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	/**
     * Other version : Update form etablissement 
	 * define an id of etablissement 
     */

	 @PutMapping("/updateV2/{id}")
	 public ResponseEntity<DTOStructure> updateV2(@PathVariable("id") final String id, @RequestBody DTOStructure structDto) {
 
		 try {
			if ( id != null) {
				Structure structure = structureService.retrieveStructureById(id);
				if (structure == null) {

					log.info("struct is null");
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}

				DTOStructure dtoStructure = structure2DTOStructure.toDTO(structure);
				dtoStructure.setStructCustomDisplayName(structDto.getStructCustomDisplayName());
				dtoStructure.setStructSiteWeb(structDto.getStructSiteWeb());
				structureService.updateStructure(dtoStructure, dtoStructure.getStructCustomDisplayName(), dtoStructure.getStructSiteWeb(), null, dtoStructure.getId());
				structureService.invalidateStructureById(id); // refresh cache 
				return new ResponseEntity<>(dtoStructure, HttpStatus.OK);
			}
			 return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		 } catch (Exception e) {
			 e.getMessage();
			 log.info("error update : ", e);
			 return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		 }
 
	 }

	@PutMapping("/updateLogo")
	public ResponseEntity<Void> updateLogo(@RequestParam("photo") MultipartFile photo, @RequestBody DTOStructure structDto) {

		try {
			if (structDto != null) {
				log.info("struct id: " + structDto.getId());
				log.info("struct dn: " + structDto.getStructCustomDisplayName());
				log.info("struct logo: " + structDto.getStructLogo());
				//structureService.updateStructure(structDto, null, null, structDto.getStructLogo(), structDto.getId());
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.getMessage();
			log.info("error update : ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

    // upload logo to disk, database, and ldap 
    @PostMapping("/fileUpload/{id}")
    public ResponseEntity<?> uploadFile(@RequestPart(name ="file", required = false) MultipartFile file, @RequestPart(name ="details") String detailsJson, @PathVariable("id") final String id) {

        try {
 
			if (detailsJson != null) {
				// Parse the detailsJson string back to DTO
    			DTOStructure dto = new Gson().fromJson(detailsJson, DTOStructure.class);

				newUrl = calculNewImageUrlPath(dto, oldUrl);
				String pathName = newUrl.getPathName();
				String getNewURL = newUrl.getUrl();
				if (newUrl != null) {
					log.info("newUrl : ", pathName);
					// save the logo to disk 
					// logoStorage.saving(pathName, file, id);
				}
				else {
					log.info("newUrl is null");
					return new ResponseEntity<>("erreur : impossible de sauvegarder l'image !", HttpStatus.BAD_REQUEST);
				}

				dto.setStructLogo(getNewURL);

				oldUrl = newUrl;
				structureService.updateStructure(dto, null, null, dto.getStructLogo(), id);
				return new ResponseEntity<>(HttpStatus.OK);
			}
            return new ResponseEntity<>("Erreur : l'établissement n'est pas défini !", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            e.getMessage();
            log.info("error upload file : ", e );
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Test API pour structLogo par default s'il n'est pas renseigné 
	 */
	@GetMapping("/test/{id}")
	public ResponseEntity<DTOStructure> testing(@PathVariable("id") final String id) {
	
		try {
			Structure structure = structureService.retrieveStructureById(id);
			if (structure == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			DTOStructure dtoStructure = structure2DTOStructure.toDTO(structure);
			oldUrl = calculOldImageUrlPath(dtoStructure);
			// manip structLogo 
			if (oldUrl != null) {
				dtoStructure.setStructLogo(oldUrl.getLocalUrl());
			}
			else {
				dtoStructure.setStructLogo(logoStorageServiceImpl.getDefaultImageLink());
			}

			return new ResponseEntity<>(dtoStructure, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("error get etab : ", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
