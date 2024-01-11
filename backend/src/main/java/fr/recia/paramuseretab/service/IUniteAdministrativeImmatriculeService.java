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
package fr.recia.paramuseretab.service;

import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;

import java.util.Collection;
import java.util.Map;

/**
 * @deprecated
 */
@Deprecated
public interface IUniteAdministrativeImmatriculeService extends IStructureService {

    /**
     * Return a Collection of UniteAdministrativeImmatriculee matching the supplied
     * codes.
     *
     * @param codes List of ids of etablissements to retrive
     * @return a never null Map of Id, only Etab wich may be empty
     * @deprecated
     */
    @Deprecated
    Map<String, UniteAdministrativeImmatriculee> retrieveEtablissementsByCodes(Collection<String> codes);

    /**
     * Return an UniteAdministrativeImmatriculee matching the supplied code.
     *
     * @param code Id of an UniteAdministrativeImmatriculee to retrieve
     * @return Etab or null
     * @deprecated
     */
    @Deprecated
    UniteAdministrativeImmatriculee retrieveEtablissementByCode(String code);

    String getSiren(String code, String name);

    String getEtabName(String id);

}
