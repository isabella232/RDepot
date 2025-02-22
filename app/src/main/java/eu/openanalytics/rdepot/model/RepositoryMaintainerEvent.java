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
package eu.openanalytics.rdepot.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.openanalytics.rdepot.api.v2.dto.EntityDto;
import eu.openanalytics.rdepot.api.v2.dto.RCreateEventDto;
import eu.openanalytics.rdepot.api.v2.dto.RRepositoryDto;
import eu.openanalytics.rdepot.api.v2.dto.RUpdateEventDto;
import eu.openanalytics.rdepot.api.v2.dto.RepositoryMaintainerDto;
import eu.openanalytics.rdepot.api.v2.dto.UpdatedVariable;
import eu.openanalytics.rdepot.api.v2.dto.UserDto;

@Entity
@Table(name="repository_maintainer_event"
    ,schema="public"
)
public class RepositoryMaintainerEvent  implements java.io.Serializable, IEventEntity
{

	private static final long serialVersionUID = 1528085596442084681L;
	private int id;
	private Date date = new Date();
    private User changedBy;
    private RepositoryMaintainer repositoryMaintainer;
    private Event event;
    private String changedVariable;
    private String valueBefore;
    private String valueAfter;
    private Date time;

    public RepositoryMaintainerEvent()
    {
    }

	
    public RepositoryMaintainerEvent(int id, Date date, User changedBy, RepositoryMaintainer repositoryMaintainer, Event event, 
    		String changedVariable, String valueBefore, String valueAfter, Date time)
    {
        this.id = id;
        this.setDate(date);
        this.changedBy = changedBy;
        this.repositoryMaintainer = repositoryMaintainer;
        this.setEvent(event);
        this.setChangedVariable(changedVariable);
        this.setValueBefore(valueBefore);
        this.setValueAfter(valueAfter);
        this.setTime(time);
    }
   
    @Id 
    @Column(name="id", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getId()
    {
        return this.id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="changed_by", nullable=false)
	//@JsonManagedReference(value="user-repository-maintainer-events")
    @JsonIgnore
    public User getChangedBy()
    {
        return this.changedBy;
    }
    
    public void setChangedBy(User changedBy)
    {
        this.changedBy = changedBy;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="repository_maintainer_id", nullable=false)
	//@JsonManagedReference(value="repository-maintainer-events")
    @JsonIgnore
    public RepositoryMaintainer getRepositoryMaintainer()
    {
        return this.repositoryMaintainer;
    }
    
    public void setRepositoryMaintainer(RepositoryMaintainer repositoryMaintainer)
    {
        this.repositoryMaintainer = repositoryMaintainer;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="date", length=13)
	public Date getDate() 
	{
		return date;
	}


	public void setDate(Date date) 
	{
		this.date = date;
	}

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="event_id", nullable=false)
	//@JsonManagedReference(value="event-repository-maintainer-events")
    @JsonIgnore
    public Event getEvent() 
	{
		return event;
	}


	public void setEvent(Event event) 
	{
		this.event = event;
	}

	@Column(name="changed_variable", nullable=false)
	public String getChangedVariable() 
	{
		return changedVariable;
	}


	public void setChangedVariable(String changedVariable) 
	{
		this.changedVariable = changedVariable;
	}

	@Column(name="value_before", nullable=false)
	public String getValueBefore() 
	{
		return valueBefore;
	}


	public void setValueBefore(String valueBefore) 
	{
		this.valueBefore = valueBefore;
	}

	@Column(name="value_after", nullable=false)
	public String getValueAfter() 
	{
		return valueAfter;
	}


	public void setValueAfter(String valueAfter) 
	{
		this.valueAfter = valueAfter;
	}
	
    @Temporal(TemporalType.TIME)
    @Column(name="time")
	public Date getTime() 
    {
		return time;
	}


	public void setTime(Date time)
	{
		this.time = time;
	}


	@Override
	public EntityDto<IEventEntity> createDto() {
		if(event.getValue() == "create") {
			return new RCreateEventDto<IEventEntity, RepositoryMaintainerDto>(this,
					id, new UserDto(changedBy), new RRepositoryDto(repositoryMaintainer.getRepository()), 
					"timestamp", new RepositoryMaintainerDto(repositoryMaintainer));
		} else if(event.getValue() == "update" || event.getValue() == "delete") {
			RUpdateEventDto<IEventEntity, RepositoryMaintainerDto> dto = 
					new RUpdateEventDto<IEventEntity, RepositoryMaintainerDto>(this,
							id, new UserDto(changedBy), new RRepositoryDto(repositoryMaintainer.getRepository()), 
							"timestamp", new RepositoryMaintainerDto(repositoryMaintainer));
			
			dto.getUpdatedVariables().add(new UpdatedVariable(changedVariable, valueBefore, valueAfter));
			
			return dto;
		} else {
			throw new IllegalArgumentException(event.getValue());
		}
	}

}


