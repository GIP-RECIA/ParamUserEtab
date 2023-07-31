package fr.recia.paramuseretab.service;

import fr.recia.paramuseretab.model.Structure;

public interface IUserService {
    
    void changeCurrentStructure(String userId, Structure struct);
}
