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
/**
 *
 */
package fr.recia.paramuseretab.dao;

import java.util.Collection;

import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.web.DTOStructure;

public interface IStructureDao {

	/**
	 * Find all structure.
	 *
	 * @return a never null Collection which may be empty
	 */
	Collection<? extends Structure> findAllStructures();

	/**
	 * Find a Structure.
	 * 
	 * @param id Id of the structure to find.
	 * @return the Structure found.
	 */
	Structure findOneStructureById(final String id);

	void saveStructure(DTOStructure dto, String customName, String siteWeb, String logo, String id);

}
