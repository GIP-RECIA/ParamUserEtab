package fr.recia.paramuseretab.service;

import java.util.Collection;
import java.util.Map;

import fr.recia.paramuseretab.model.Groups;

public interface IGroupService {

    Collection<? extends Groups> retrieveGroups();

}
