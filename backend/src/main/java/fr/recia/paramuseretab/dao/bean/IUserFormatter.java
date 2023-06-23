package fr.recia.paramuseretab.dao.bean;

import java.util.List;

import fr.recia.paramuseretab.model.Person;

public interface IUserFormatter {
    
    List<Person> formatPerson(List<Person> person);
}
