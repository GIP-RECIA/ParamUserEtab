package com.example.web;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

import com.example.model.Structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DTOStructure extends Structure  {

    @Size(max = 25, message = "custom name must not exceed 25 characters.")
    private String structCustomDisplayName;

    private String structLogo; 

    private String structSiteWeb; 

    public DTOStructure(String id, String name, String displayName, String description, Map<String, List<String>> otherAttributes,  String structCustomDisplayName, String structLogo, String structSiteWeb) {
        super(id, name, displayName, description, otherAttributes);
        this.structCustomDisplayName = structCustomDisplayName;
        this.structLogo = structLogo;
        this.structSiteWeb = structSiteWeb;
    }
    
}
