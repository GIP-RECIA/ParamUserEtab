/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.paramuseretab.service.impl;

import fr.recia.paramuseretab.dao.IUserDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
