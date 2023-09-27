package fr.recia.paramuseretab.web;

import java.util.List;
import java.util.Map;

import fr.recia.paramuseretab.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DTOPerson extends Person {

    private List<Map<String, String>> listEtab;

    public DTOPerson(String uid, String currentStruct, List<String> structIds, List<String> nameEtab,
            List<Map<String, String>> listEtab) {
        super(uid, currentStruct, structIds, nameEtab);
        this.listEtab = listEtab;
    }
}
