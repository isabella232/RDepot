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
package eu.openanalytics.rdepot.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;

import eu.openanalytics.rdepot.model.User;

public class AuthException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6885176933074114068L;

	public AuthException(String messageCode, MessageSource messageSource, Locale locale, User user) {
		super(user.getLogin() + ": " + messageSource.getMessage(
						messageCode, null, 
						messageCode, locale));
	}
	
	public AuthException(String messageCode, MessageSource messageSource, Locale locale, int userId) {
		super("User " + userId + ": " + messageSource.getMessage(
						messageCode, null, 
						messageCode, locale));
	}
	
	public AuthException(String messageCode) {
		super(messageCode);
	}
}
