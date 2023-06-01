package com.example.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.example.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonAttributesMapper implements AttributesMapper<Person> {

    private String uid; 
    private String currentStruct;
    private Set<String> groupAttributes;

    @Override
    @SuppressWarnings (value="unchecked")
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = new Person();
        person.setUid((String) attributes.get(uid).get());
        person.setCurrentStruct((String) attributes.get(currentStruct).get());

        List<Map<String, String>> listGroups = new ArrayList<>();
        for (String attrName : groupAttributes) {
            final Attribute dirAttr = attributes.get(attrName);
            if (dirAttr != null) {
                for (NamingEnumeration<String> ae = (NamingEnumeration<String>) dirAttr.getAll(); ae.hasMore();) {
                    Map<String, String> grpItem = new HashMap<>();
                    grpItem.put("etabName", ae.next());
                    listGroups.add(grpItem);
                }
            }
        }

        person.setIsMemberOf(listGroups);

        return person;
    }
}