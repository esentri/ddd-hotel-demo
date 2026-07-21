/*
 *  Copyright 2026 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.esentri.rezeption;

import io.domainlifecycles.autoconfig.annotation.EnableDlc;
import io.domainlifecycles.validation.extend.ValidationDomainClassExtender;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableDlc(
dlcMirrorBasePackages = "com.esentri.rezeption",
jooqRecordPackage = "com.esentri.rezeption.records",
jooqSqlDialect = "H2")
public class RezeptionsServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RezeptionsServicesApplication.class, args);
	}

	/**
	 * Enable DLC byte code extension for the domain model of the "sampleshop"
	 */
	@PostConstruct
	public void postConstruct() {
		ValidationDomainClassExtender.extend("com.esentri.rezeption");
	}
}
