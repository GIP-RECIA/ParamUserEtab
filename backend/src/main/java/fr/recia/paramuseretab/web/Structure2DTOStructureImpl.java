package fr.recia.paramuseretab.web;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import fr.recia.paramuseretab.model.Structure;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Structure2DTOStructureImpl {

    public DTOStructure toDTO(Structure struct) {

        String structCustomDisplayName = null;
        if ( !struct.getName().equals(struct.getDisplayName()) ) {
            structCustomDisplayName = struct.getDisplayName();
        } 

        Map<String, List<String>> otherAttributes = struct.getOtherAttributes();
        String structSiteWeb = null;
        String structLogo = null;
        for (Map.Entry<String, List<String>> entry : otherAttributes.entrySet()) {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();

            if (key.equals("ESCOStructureLogo") && valueList != null && !valueList.isEmpty() ) {
                structLogo =  valueList.get(0);
            }
            if (key.equals("ENTStructureSiteWeb") && valueList != null && !valueList.isEmpty() ) {
                structSiteWeb = valueList.get(0);
            }
        }

        log.debug(structLogo, structCustomDisplayName, structSiteWeb);

        final DTOStructure dto = new DTOStructure();
        dto.setId(struct.getId());
        dto.setName(struct.getName());
        dto.setDisplayName(struct.getDisplayName());
        dto.setDescription(struct.getDescription());
        dto.setOtherAttributes(struct.getOtherAttributes());
        dto.setStructCustomDisplayName(structCustomDisplayName);
        dto.setStructLogo(structLogo);
        dto.setStructSiteWeb(structSiteWeb);
        
        
        return dto;

    }
}
