package fr.recia.paramuseretab.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.service.IUniteAdministrativeImmatriculeService;

@Service
public class Person2DTOPersonImpl {

    @Autowired
    private IUniteAdministrativeImmatriculeService uai;

    public DTOPerson toDTOParamEtab(Person person) {

        List<Map<String, String>> listEtab = new ArrayList<>();

        // TO DO: retrieve the name of etab for getting the idSiren
        if (!person.getIsMemberOf().isEmpty()) {

            List<String> etabNames = person.getIsMemberOf();
            for (String value : etabNames) {
                String name = null;
                Map<String, String> itemMap = new HashMap<>();

                // find the last occurence of '('
                int startIndex = value.lastIndexOf('(');

                if (startIndex != -1) {
                    // extract the substring from startIndex + 1 to the end
                    String result = value.substring(startIndex + 1, value.length() - 1);

                    name = this.uai.getSiren(result, null);
                } else {
                    name = this.uai.getSiren(null, value);
                }

                itemMap.put("idSiren", name);
                itemMap.put("etabName", value);
                listEtab.add(itemMap);
            }

        }

        final DTOPerson dto = new DTOPerson();
        dto.setUid(person.getUid());
        dto.setCurrentStruct(person.getCurrentStruct());
        dto.setSiren(person.getSiren());
        dto.setIsMemberOf(person.getIsMemberOf());
        dto.setListEtab(listEtab);

        return dto;
    }

    public DTOPerson toDTOChangeEtab(Person person) {

        List<Map<String, String>> result = new ArrayList<>();

        // TO DO: retrieve the siren for getting the displayname of etab (structure)
        if (!person.getSiren().isEmpty()) {

            List<String> ids = person.getSiren();
            String etabName = null;
            for (String idSiren : ids) {
                etabName = this.uai.getEtabName(idSiren);
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("idSiren", idSiren);
                itemMap.put("etabName", etabName);
                result.add(itemMap);

            }

        }

        final DTOPerson dto = new DTOPerson();
        dto.setUid(person.getUid());
        dto.setCurrentStruct(person.getCurrentStruct());
        dto.setSiren(person.getSiren());
        dto.setIsMemberOf(person.getIsMemberOf());
        dto.setListEtab(result);

        return dto;
    }
}
