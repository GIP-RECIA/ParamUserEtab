/*
 * Copyright (C) 2017 GIP RECIA http://www.recia.fr
 * @Author (C) 2013 Maxime Bossard <mxbossard@gmail.com>
 * @Author (C) 2016 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.example.dao;

import java.util.Collection;

import com.example.model.Structure;

/**
 * @author GIP RECIA - Julien Gribonvald.
 *
 */
public interface IStructureDao {

	/**
	 * Find all structure.
	 *
	 * @return a never null Collection which may be empty
	 */
	Collection<? extends Structure> findAllStructures();


	/**
	 * Find a Structure.
	 * @param id Id of the structure to find.
	 * @return the Structure found.
	 */
	Structure findOneStructureById(final String id);

	void saveStructure(String customName, String siteWeb, String logo, String id);

}
