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
package eu.openanalytics.rdepot.formatter;

import org.springframework.core.convert.converter.Converter;

import eu.openanalytics.rdepot.api.v2.dto.SubmissionState;

public class StringToSubmissionStateConverter implements Converter<String, SubmissionState> {

	@Override
	public SubmissionState convert(String source) {
		try {
			return SubmissionState.valueOf(source.toUpperCase());
		} catch(IllegalArgumentException e) {
			return null;
		}
	}

}
