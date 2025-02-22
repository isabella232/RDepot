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
package eu.openanalytics.rdepot.test.unit.api.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.openanalytics.rdepot.api.v2.controller.RRepositoryController;
import eu.openanalytics.rdepot.api.v2.dto.RRepositoryDto;
import eu.openanalytics.rdepot.exception.RepositoryCreateException;
import eu.openanalytics.rdepot.exception.RepositoryDeleteException;
import eu.openanalytics.rdepot.messaging.MessageCodes;
import eu.openanalytics.rdepot.model.Repository;
import eu.openanalytics.rdepot.model.User;
import eu.openanalytics.rdepot.test.context.TestConfig;
import eu.openanalytics.rdepot.test.fixture.RRepositoryTestFixture;

@ContextConfiguration(classes = {TestConfig.class})
@WebMvcTest(RRepositoryController.class)
@ActiveProfiles("apiv2")
@WebAppConfiguration
@Import({TestConfig.class})
public class RRepositoryControllerTest extends ApiV2ControllerUnitTest {
	
	private static final String JSON_PATH = "src/test/resources/unit/jsons";
	public static final String EXAMPLE_REPOSITORIES_PATH = JSON_PATH + "/example_repositories.json";
	public static final String EXAMPLE_REPOSITORY_PATH = JSON_PATH + "/example_repository.json";
	public static final String EXAMPLE_NEW_REPOSITORY_PATH = JSON_PATH + "/example_new_repository.json";
	public static final String ERROR_REPOSITORY_NOT_FOUND = JSON_PATH + "/error_repository_notfound.json";
	public static final String EXAMPLE_REPOSITORY_CREATED = JSON_PATH + "/example_repository_created.json";
	public static final String ERROR_VALIDATION_REPOSITORY_NAME_PATH = JSON_PATH + "/error_validation_repository_name.json";
	public static final String EXAMPLE_REPOSITORY_PATCHED_PATH = JSON_PATH + "/example_repository_patched.json";
	public static final String ERROR_REPOSITORY_NOT_FOUND_PATH = JSON_PATH + "/error_repository_notfound.json";
	public static final String ERROR_REPOSITORY_MALFORMED_PATCH = JSON_PATH + "/error_repository_malformed_patch.json";

	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	MappingJackson2HttpMessageConverter jsonConverter;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	RRepositoryController rRepositoryController;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@Test
	public void getAllRepositories() throws Exception {
		User user = getAdminAndAuthenticate(userService);
		Principal principal = getMockPrincipal(user);
		
		when(repositoryService.findAll(any()))
			.thenReturn(RRepositoryTestFixture
					.GET_EXAMPLE_REPOSITORIES_PAGED());
		when(userService.findByLogin(principal.getName()))
			.thenReturn(user);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.get("/api/v2/manager/r/repositories")
					.contentType(MediaType.APPLICATION_JSON)
					.principal(principal))
			.andExpect(
					status()
					.isOk())
			.andExpect(content().json(
					Files.readString(Path.of(EXAMPLE_REPOSITORIES_PATH))));
	}
	
	@Test
	public void getAllRepositories_returns401_whenUserIsNotAuthenticated() throws Exception {
		mockMvc
			.perform(MockMvcRequestBuilders
					.get("/api/v2/manager/r/repositories")
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHENTICATED_PATH))));
	}
	
	@Test
	public void getRepository() throws Exception {
		final User user = getUserAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Repository repository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY();
		final Integer ID = repository.getId();
		
		when(repositoryService.findById(ID)).thenReturn(repository);
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.get("/api/v2/manager/r/repositories/" + ID)
					.contentType(MediaType.APPLICATION_JSON)
					.principal(principal))
			.andExpect(status().isOk())
			.andExpect(content().json(Files.readString(Path.of(EXAMPLE_REPOSITORY_PATH))));
	}	
	
	@Test
	public void getRepository_returns401_whenUserIsNotAuthenticated() throws Exception {
		mockMvc
			.perform(MockMvcRequestBuilders
					.get("/api/v2/manager/r/repositories/" + 123)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHENTICATED_PATH))));
	}

	@Test
	public void getRepository_returns404_whenRepositoryIsNotFound() throws Exception {
		final User user = getUserAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		when(repositoryService.findById(anyInt())).thenReturn(null);
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.get("/api/v2/manager/r/repositories/" + 123)
					.contentType(MediaType.APPLICATION_JSON)
					.principal(principal))
			.andExpect(status().isNotFound())
			.andExpect(content().json(Files.readString(Path.of(ERROR_REPOSITORY_NOT_FOUND))));
	}
	
	@Test
	public void createRepository() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		final Path path = Path.of(EXAMPLE_NEW_REPOSITORY_PATH);
		final String exampleJson = Files.readString(path);
		
		final Repository createdRepository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY(100);
		
		when(repositoryService.create(any(), eq(user))).thenReturn(createdRepository);
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		doNothing().when(repositoryValidator).validate(any(), any());
		when(repositoryValidator.supports(Repository.class)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.post("/api/v2/manager/r/repositories")
					.content(exampleJson)
					.contentType(MediaType.APPLICATION_JSON)
					.principal(principal))
			.andExpect(status().isCreated())
			.andExpect(content().json(Files.readString(Path.of(EXAMPLE_REPOSITORY_CREATED))));
		
		verify(repositoryService, times(1)).create(any(), eq(user));
	}
	
	@Test
	public void createRepository_returns401_whenUserIsNotAuthenticated() throws Exception {
		final Path path = Path.of(EXAMPLE_NEW_REPOSITORY_PATH);
		final String exampleJson = Files.readString(path);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.post("/api/v2/manager/repositories")
					.contentType(MediaType.APPLICATION_JSON)
					.content(exampleJson))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHENTICATED_PATH))));
	}
	
	@Test
	public void createRepository_returns403_whenUserIsNotAuthorized() throws Exception {
		final User user = getUserAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Path path = Path.of(EXAMPLE_NEW_REPOSITORY_PATH);
		final String exampleJson = Files.readString(path);
		
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		
		mockMvc
			.perform(MockMvcRequestBuilders
				.post("/api/v2/manager/r/repositories")
				.contentType("application/json")
				.content(exampleJson)
				.principal(principal))
			.andExpect(status().isForbidden())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHORIZED_PATH))));
	}			
	
	@Test
	public void createRepository_returns422_whenRepositoryValidationFails() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		final Path path = Path.of(EXAMPLE_NEW_REPOSITORY_PATH);
		final String exampleJson = Files.readString(path);
				
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		doAnswer(invocation -> {
			Errors errors = (Errors)invocation.getArgument(1);
			
			errors.rejectValue("name", MessageCodes.ERROR_FORM_DUPLICATE_NAME);
			return null;
		}).when(repositoryValidator).validate(any(), any());
		when(repositoryValidator.supports(Repository.class)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.post("/api/v2/manager/r/repositories")
					.contentType(MediaType.APPLICATION_JSON)
					.content(exampleJson)
					.principal(principal))
			.andExpect(
					status().isUnprocessableEntity())
			.andExpect(
					content().json(Files.readString(Path.of(ERROR_VALIDATION_REPOSITORY_NAME_PATH))));
	}
	
	@Test
	public void createRepository_returns500_whenCreationFails() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		final Path path = Path.of(EXAMPLE_NEW_REPOSITORY_PATH);
		final String exampleJson = Files.readString(path);
		
		final Repository newRepository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY(0);
		
		doThrow(new RepositoryCreateException(messageSource, Locale.ENGLISH, newRepository))
			.when(repositoryService).create(any(), eq(user));
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		doNothing().when(repositoryValidator).validate(any(), any());
		when(repositoryValidator.supports(Repository.class)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.post("/api/v2/manager/r/repositories")
					.content(exampleJson)
					.contentType(MediaType.APPLICATION_JSON)
					.principal(principal))
			.andExpect(status().isInternalServerError())
			.andExpect(content().json(Files.readString(Path.of(ERROR_CREATE_PATH))));
		
		verify(repositoryService, times(1)).create(any(), eq(user));
	}
	
	@Test
	public void patchRepository() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Repository repository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY();
		final Integer ID = repository.getId();
		
		final String patchJson = "[{\"op\": \"replace\",\"path\":\"/serverAddress\",\"value\":\"127.0.0.1\"}]";
		when(repositoryService.findById(ID)).thenReturn(repository);
		when(userService.findByLoginWithRepositoryMaintainers(principal.getName())).thenReturn(user);
		when(userService.isAuthorizedToEdit(repository, user)).thenReturn(true);
		when(repositoryService.evaluateAndUpdate(any(), eq(user))).thenAnswer(new Answer<Repository>() {

			@Override
			public Repository answer(InvocationOnMock invocation) throws Throwable {
				RRepositoryDto dto = (RRepositoryDto)invocation.getArgument(0);
				
				assertEquals("127.0.0.1", dto.getServerAddress());
				repository.setServerAddress("127.0.0.1");
				return repository;
			}
			
		});
		doNothing().when(repositoryValidator).validate(any(), any());
		when(repositoryValidator.supports(Repository.class)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
				.patch("/api/v2/manager/r/repositories/" + ID)
				.content(patchJson)
				.contentType("application/json-patch+json")
				.principal(principal))
			.andExpect(status().isOk())
			.andExpect(content().json(Files.readString(Path.of(EXAMPLE_REPOSITORY_PATCHED_PATH))));
	}	
	
	@Test
	public void patchRepository_returns401_whenUserIsNotAuthenticated() throws Exception {
		String patchJson = "[{\"op\": \"replace\",\"path\":\"/serverAddress\",\"value\":\"127.0.0.1\"}]";

		mockMvc
			.perform(MockMvcRequestBuilders
				.patch("/api/v2/manager/r/repositories/" + 123)
				.contentType("application/json-patch+json")
				.content(patchJson))
			.andExpect(status().isUnauthorized())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHENTICATED_PATH))));
	}
	
	@Test
	public void patchRepository_returns403_whenUserIsNotAuthorized() throws Exception {
		final User user = getUserAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		when(userService.isAuthorizedToEdit(any(Repository.class), eq(user))).thenReturn(false);
		
		String patchJson = "[{\"op\": \"replace\",\"path\":\"/serverAddress\",\"value\":\"127.0.0.1\"}]";
		
		mockMvc
			.perform(MockMvcRequestBuilders
				.patch("/api/v2/manager/r/repositories/" + 123)
				.contentType("application/json-patch+json")
				.content(patchJson)
				.principal(principal))
			.andExpect(status().isForbidden())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHORIZED_PATH))));
	}
	
	@Test
	public void patchRepository_returns404_whenRepositoryIsNotFound() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		when(repositoryService.findById(anyInt())).thenReturn(null);
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		
		String patchJson = "[{\"op\": \"replace\",\"path\":\"/serverAddress\",\"value\":\"127.0.0.1\"}]";
		
		mockMvc
			.perform(MockMvcRequestBuilders
				.patch("/api/v2/manager/r/repositories/" + 123)
				.contentType("application/json-patch+json")
				.principal(principal)
				.content(patchJson))
			.andExpect(status().isNotFound())
			.andExpect(content().json(Files.readString(Path.of(ERROR_REPOSITORY_NOT_FOUND_PATH))));
	}
	
	@Test
	public void patchRepository_returns422_whenPatchIsIncorrect() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Repository repository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY();
		final Integer ID = repository.getId();
		
		final String patchJson = "[{\"op\": \"replace\",\"path\":\"/nameeeeee\",\"value\":\"Test Repo 123\"}]";
		
		when(repositoryService.findById(ID)).thenReturn(repository);
		when(userService.findByLoginWithRepositoryMaintainers(principal.getName())).thenReturn(user);
		when(userService.isAuthorizedToEdit(repository, user)).thenReturn(true);
		
		mockMvc
		.perform(MockMvcRequestBuilders
				.patch("/api/v2/manager/r/repositories/" + ID)
				.contentType("application/json-patch+json")
				.content(patchJson)
				.principal(principal))
		.andExpect(
				status().isUnprocessableEntity())
		.andExpect(
				content().json(Files.readString(Path.of(ERROR_REPOSITORY_MALFORMED_PATCH))));
	}
	
	@Test
	public void patchRepository_returns422_whenRepositoryValidationFails() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Repository repository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY();
		final Integer ID = repository.getId();
		final String patchJson = "[{\"op\": \"replace\",\"path\":\"/name\",\"value\":\"Test Repo 123\"}]";
		
		when(repositoryService.findById(ID)).thenReturn(repository);
		when(userService.findByLoginWithRepositoryMaintainers(principal.getName())).thenReturn(user);
		when(userService.isAuthorizedToEdit(repository, user)).thenReturn(true);
		doAnswer(invocation -> {
			Errors errors = (Errors)invocation.getArgument(1);
			
			errors.rejectValue("name", MessageCodes.ERROR_FORM_DUPLICATE_NAME);
			return null;
		}).when(repositoryValidator).validate(any(), any());
		when(repositoryValidator.supports(Repository.class)).thenReturn(true);
		
		mockMvc
		.perform(MockMvcRequestBuilders
				.patch("/api/v2/manager/r/repositories/" + ID)
				.contentType("application/json-patch+json")
				.content(patchJson)
				.principal(principal))
		.andExpect(
				status().isUnprocessableEntity())
		.andExpect(
				content().json(Files.readString(Path.of(ERROR_VALIDATION_REPOSITORY_NAME_PATH))));
	}
	
	@Test
	public void deleteRepository() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Repository repository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY();
		final Integer ID = repository.getId();
		
		repository.setDeleted(true);
		
		when(repositoryService.findByIdAndDeleted(ID, true)).thenReturn(repository);
		when(repositoryService.shiftDelete(repository)).thenReturn(null);
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		when(userService.isAdmin(user)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.delete("/api/v2/manager/r/repositories/" + ID)
					.principal(principal))
			.andExpect(status().isNoContent());
		
		verify(repositoryService, times(1)).shiftDelete(repository);
	}	
	
	@Test
	public void deleteRepository_returns401_whenUserIsNotAuthenticated() throws Exception {
		mockMvc
			.perform(MockMvcRequestBuilders
					.delete("/api/v2/manager/r/repositories/" + 123))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void deleteRepository_returns403_whenUserIsNotAuthorized() throws Exception {
		final User user = getUserAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.delete("/api/v2/manager/r/repositories/" + 123)
					.principal(principal))
			.andExpect(status().isForbidden())
			.andExpect(content().json(Files.readString(Path.of(ERROR_NOT_AUTHORIZED_PATH))));
	}
	
	@Test
	public void deleteRepository_returns404_whenRepositoryIsNotFound() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		
		when(repositoryService.findByIdAndDeleted(anyInt(), eq(true))).thenReturn(null);
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		when(userService.isAdmin(user)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.delete("/api/v2/manager/r/repositories/" + 123))
			.andExpect(status().isNotFound())
			.andExpect(content().json(Files.readString(Path.of(ERROR_REPOSITORY_NOT_FOUND))));
		
	}
	
	@Test
	public void deleteRepository_returns500_whenRepositoryDeletionFails() throws Exception {
		final User user = getAdminAndAuthenticate(userService);
		final Principal principal = getMockPrincipal(user);
		final Repository repository = RRepositoryTestFixture.GET_EXAMPLE_REPOSITORY();
		final Integer ID = repository.getId();
		
		repository.setDeleted(true);
		
		when(repositoryService.findByIdAndDeleted(ID, true)).thenReturn(repository);
		doThrow(new RepositoryDeleteException(messageSource, Locale.ENGLISH, repository))
			.when(repositoryService).shiftDelete(any());
		when(userService.findByLogin(principal.getName())).thenReturn(user);
		when(userService.isAdmin(user)).thenReturn(true);
		
		mockMvc
			.perform(MockMvcRequestBuilders
					.delete("/api/v2/manager/r/repositories/" + ID)
					.principal(principal))
			.andExpect(status().isInternalServerError())
			.andExpect(content().json(Files.readString(Path.of(ERROR_DELETE_PATH))));
	}
	
	
}