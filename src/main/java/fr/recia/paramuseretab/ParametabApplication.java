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

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication(exclude = ThymeleafAutoConfiguration.class)
@ComponentScan(basePackages = "fr.recia.paramuseretab.*")
@EnableConfigurationProperties
public class ParametabApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(ParametabApplication.class);
        app.setBannerMode(Banner.Mode.OFF);

        Environment env = app.run(args).getEnvironment();
        log.info(
            "Access URLs:" +
                "\n----------------------------------------------------------" +
                "\n\tLocal: \t\thttp://127.0.0.1:{}" +
                "\n\tExternal: \thttp://{}:{}" +
                "\n\tProfiles: \t{}" +
                "\n----------------------------------------------------------",
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getProperty("spring.profiles.active")
        );
    }

}
