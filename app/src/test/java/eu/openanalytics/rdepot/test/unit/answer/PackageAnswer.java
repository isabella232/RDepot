/**
 * R Depot
 *
 * Copyright (C) 2012-2021 Open Analytics NV
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.rdepot.test.unit.answer;

import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import eu.openanalytics.rdepot.model.Package;
import eu.openanalytics.rdepot.model.Repository;

public class PackageAnswer implements Answer<Package> {

	Map<String, Map<String, Package>> mockRepositories;
	
	public PackageAnswer(Map<String, Map<String, Package>> mockRepositories) {
		this.mockRepositories = mockRepositories;
	}
	
	@Override
	public Package answer(InvocationOnMock invocation) throws Throwable {
		Object[] arguments = invocation.getArguments();
		String name;
		String version;
		Repository repository;
		Map<String, Package> repositoryMap = null;
		
		if(arguments.length > 0) {
			name = (String) arguments[0];
			
			if(arguments.length == 2) {
				repository = (Repository) arguments[1];
				repositoryMap = mockRepositories.get(repository.getName());
				
				if(repositoryMap != null) {
					for(String packageName : repositoryMap.keySet()) {
						if(packageName.startsWith(name)) {
							return repositoryMap.get(packageName);
						}
					}
				}
				
			} else {
				version = (String) arguments[1];
				repository = (Repository) arguments[2];
				repositoryMap = mockRepositories.get(repository.getName());
				
				return repositoryMap.get(name + "_" + version);
			}	
		}
		return null;
	}

}
