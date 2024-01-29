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
package fr.recia.paramuseretab;

import fr.recia.paramuseretab.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application
            .profiles(addDefaultProfile())
            .bannerMode(Banner.Mode.OFF)
            .sources(ParametabApplication.class);
    }

    private String addDefaultProfile() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile != null) {
            log.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        log.warn("No Spring profile configured, running with default configuration");
        return Constants.SPRING_PROFILE_DEVELOPMENT;
    }

}
