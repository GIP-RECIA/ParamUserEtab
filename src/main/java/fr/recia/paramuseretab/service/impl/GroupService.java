package fr.recia.paramuseretab.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.recia.paramuseretab.dao.IGroupDao;
import fr.recia.paramuseretab.model.Groups;
import fr.recia.paramuseretab.service.IGroupService;

@Service
public class GroupService implements IGroupService {

    @Autowired
    private IGroupDao groupDao;

    @Override
    public Collection<? extends Groups> retrieveGroups() {

        final Collection<? extends Groups> allgroups = this.groupDao.findAllGroups();

        return allgroups;

    }

}
