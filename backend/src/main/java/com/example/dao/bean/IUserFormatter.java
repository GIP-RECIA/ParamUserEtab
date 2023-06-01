package com.example.dao.bean;

import java.util.List;

import com.example.model.Person;

public interface IUserFormatter {
    
    List<Person> formatPerson(List<Person> person);
}
