package com.example.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.service.IImageUrlPath;
import com.example.service.ILogoStorageService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
@ConfigurationProperties(prefix = "logostorage")
public class LogoStorageServiceImpl implements ILogoStorageService  {


    private String prefixImagePath;
    private String prefixImageLink; 
    private String prefixImageName;
    private String prefixLocalUrl; 
    private String defaultImageLink;
    private String formatImage;
    private final Path root;

    protected class ImageUrlPath implements IImageUrlPath {

        protected String url;
        protected String localUrl;

        protected String version;
        protected String pathName;
        protected String pathDir;
        protected String pathUser;
        protected String format;

        @Override
        public String getLocalUrl() {
            return localUrl;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getPathName() {
            return pathName;
        }

        @Override
        public String getPathDir() {
            return pathDir;
        }

        @Override
        public String getPathUser() {
            return pathUser;
        }

        @Override
        public String getFormat() {
            return format;
        }

    }

    @Autowired
    public LogoStorageServiceImpl(Environment env) {

        root = Paths.get(env.getProperty("app.file.upload-dir", "./uploads/files")).toAbsolutePath().normalize();

        try {
            Files.createDirectories(root);
        } catch (Exception e) {
            log.info("error create folder", e);
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
          return null;
        }
        String[] fileNameParts = fileName.split("\\.");
    
        return fileNameParts[fileNameParts.length - 1];
      }

    /**
     * TO DO :
     *  1) ok. il faut définir le chemin du repertoire de stockage de l'image :
     *      etab.prefix.image.path = /mnt/vol_ent/annuaire_images/logos ( machine pour stocker les logos )
     *  2) ok. définir aussi le prefix de l'url de l'image sans idSiren : 
     *      etab.prefix.image.link = /annuaires_images/logos 
     *  3) créer des méthodes qui permettent de créer l'url path de l'image pour pouvoir le stocker dans db et ldap ???? 
     *  4)  
     *
     */

    // save file 
    @Override
    public String save(MultipartFile logo, String id) {

        // Determine the file name
        String originalFileName = logo.getOriginalFilename(); // or generate a unique file name

        String fileName = "logoportail0." + getFileExtension(originalFileName);

        // Construct the destination path
        String destinationPath = "uploads/file/" + id + "/" + fileName;

        // Save the file to disk
        try {
            // Ensure the directories exist
            Path directory = Paths.get("uploads/file/" + id);
            Files.createDirectories(directory);

            // Save the file
            Path filePath = Paths.get(destinationPath);
            Files.write(filePath, logo.getBytes());
            System.out.println(filePath);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }

        // Return the file path
        return destinationPath;
    }

    @Override
    public void saving(String pathName, MultipartFile logo, String id) {

        // Save the file to disk
        try {
            // Ensure the directories exist
            Path directory = Paths.get("uploads/file/" + id);
            Files.createDirectories(directory);

            System.out.println("### pathName saving : " + pathName);

            // Save the file
            if (pathName != null) {
                Path destinationPath = Paths.get(pathName);
                Files.write(destinationPath, logo.getBytes());
                System.out.println(destinationPath);
            }
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            System.out.println("error create file : " + e);
        }
    }

    @Override
    public IImageUrlPath makeImageUrlPath(String nom, int version) {
       
        if (nom != null) {
            int length = nom.length();
            if (length >= 2) {
                return makeImageUrlPath(nom, formatImage, version);
            }
        }
        return null;
    }

    private ImageUrlPath makeImageUrlPath(String userDir, String format, int version) {
        int vers = version % 100;
        int parite = vers % 2;
        return makeImageUrlPath(userDir, 
                    String.format("%s%d.%s", prefixImageName, parite,  format), 
                    format, 
                    String.format("%d", vers)
                );
    }

    private  ImageUrlPath makeImageUrlPath (String userDir, String fileName, String format, String version) { 
        String relativePathName; 
        ImageUrlPath iup = new ImageUrlPath();
        iup.format = format;
        iup.version = version;
        iup.pathDir = prefixImagePath;
        iup.pathUser = String.format("%s/%s", prefixImagePath, userDir);
        relativePathName = String.format("/%s/%s", userDir, fileName);

        iup.url = String.format("%s%s?%s", prefixImageLink, relativePathName, version );

        iup.pathName = String.format("%s%s", prefixImagePath, relativePathName);
        iup.localUrl = String.format("%s%s", prefixLocalUrl , relativePathName);
        System.out.println("iup.url : " +iup.url);
        System.out.println("iup.pathName : " +iup.pathName);
        return iup;
    }

    @Override
    public IImageUrlPath makeImageUrlPath(String url) {
        ImageUrlPath iup = null;
        int nbMinElem = 2;
        if (url != null) {
            try {
            String[] dirs = url.split("/");
            int taille = dirs.length;
            if (taille >= nbMinElem) {
                String[] lastNameVersion  = dirs[taille-1].split("\\?");
                int formatIdx = lastNameVersion[0].lastIndexOf(".");
                String format = null;
                if(formatIdx >0 ) { 
                    format = lastNameVersion[0].substring(formatIdx+1);
                }
                iup = makeImageUrlPath(
                            dirs[taille-2], 
                            lastNameVersion[0], 
                            format, 
                            lastNameVersion[1]
                        );
                    // on remet l'url en enrée 
                iup.url = url;
                System.out.println("iup : " + iup);
            }
            } catch (Exception e) {
                
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return iup;
    }


}
