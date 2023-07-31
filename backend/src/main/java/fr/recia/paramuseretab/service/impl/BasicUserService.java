package fr.recia.paramuseretab.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import fr.recia.paramuseretab.dao.IUserDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IUserService;

@Service
public class BasicUserService implements IUserService {
    
    @Autowired
    private IUserDao userDao;

    @Override
    public void changeCurrentStructure(String userId, Structure struct) {
        Assert.hasText(userId, "No user Id supplied !");
		Assert.notNull(struct, "No structure supplied !");

		this.userDao.saveCurrentStructure(userId, struct);
    }
}
