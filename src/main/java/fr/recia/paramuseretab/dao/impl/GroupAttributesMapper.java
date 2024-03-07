package fr.recia.paramuseretab.dao.impl;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import fr.recia.paramuseretab.model.Groups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class GroupAttributesMapper implements AttributesMapper {

    @NonNull
    private final String nameAttrKey;

    @Override
    public Object mapFromAttributes(final Attributes attributes) throws NamingException {

        Groups group;

        final String name = (String) attributes.get(this.nameAttrKey).get();

        group = new Groups(name);

        return group;
    }

}
