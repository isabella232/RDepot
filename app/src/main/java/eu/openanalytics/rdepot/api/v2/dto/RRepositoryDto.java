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
package eu.openanalytics.rdepot.api.v2.dto;

import eu.openanalytics.rdepot.model.Repository;

public class RRepositoryDto extends EntityDto<Repository> {
	
	private Integer id = 0;
	private Integer version = 0;
	private String publicationUri;
	private String name;
	private String serverAddress = "127.0.0.1";
	private Boolean deleted = false;
	private Boolean published = false;
	private Boolean synchronizing = false;
	
	public RRepositoryDto(Repository repository) {
		super(repository);
		this.id = repository.getId();
		this.version = repository.getVersion();
		this.publicationUri = repository.getPublicationUri();
		this.name = repository.getName();
		this.serverAddress = repository.getServerAddress();
		this.deleted = repository.isDeleted();
		this.published = repository.isPublished();
		this.synchronizing = repository.isSynchronizing();
	}
	
	public RRepositoryDto() {
		
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public String getPublicationUri() {
		return publicationUri;
	}
	
	public void setPublicationUri(String publicationUri) {
		this.publicationUri = publicationUri;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getServerAddress() {
		return serverAddress;
	}
	
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public Boolean isDeleted() {
		return deleted;
	}
	
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public Boolean isPublished() {
		return published;
	}
	
	public void setPublished(Boolean published) {
		this.published = published;
	}
	
	public Boolean isSynchronizing() {
		return synchronizing;
	}
	
	public void setSynchronizing(Boolean synchronizing) {
		this.synchronizing = synchronizing;
	}

	@Override
	public Repository toEntity() {
		Repository repository = new Repository();
		repository.setId(id);
		repository.setName(name);
		repository.setPublicationUri(publicationUri);
		repository.setServerAddress(serverAddress);
		repository.setVersion(version);
		repository.setPublished(published);
		repository.setSynchronizing(synchronizing);
		repository.setDeleted(deleted);
		
		return repository;
	}

}