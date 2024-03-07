package fr.recia.paramuseretab.dao;

import fr.recia.paramuseretab.model.Groups;
import java.util.Collection;

public interface IGroupDao {

    Collection<? extends Groups> findAllGroups();

}
