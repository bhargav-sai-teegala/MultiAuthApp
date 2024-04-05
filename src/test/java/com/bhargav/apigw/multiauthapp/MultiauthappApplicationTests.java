package com.bhargav.apigw.multiauthapp;

import com.bhargav.apigw.multiauthapp.config.CustomAuthenticationFailureHandler;
import com.bhargav.apigw.multiauthapp.config.CustomPasswordEncoder;
import com.bhargav.apigw.multiauthapp.controller.LoginController;
import com.bhargav.apigw.multiauthapp.entity.LoginAttempt;
import com.bhargav.apigw.multiauthapp.repository.LoginAttemptRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
class MultiauthappApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "admin", password = "admin", roles = "USER")
	void testSuccessLoginWithUsernameAndPassword() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "admin")
						.param("password", "admin"))
				.andExpect(redirectedUrl("/welcome"));
	}

	@Test
	@WithMockUser(username = "user1", password = "user2", roles = "USER")
	void testFailureLoginWithUsernameAndPassword() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "user1")
						.param("password", "user2"))
				.andExpect(redirectedUrl("/login-error?username=user1"));
	}

	@Test
	void testWelcomeGoogle() {
		LoginController controller = new LoginController();

		OAuth2AuthenticationToken mockPrincipal = Mockito.mock(OAuth2AuthenticationToken.class);
		Model mockModel = Mockito.mock(Model.class);
		OAuth2User mockOAuth2User = Mockito.mock(OAuth2User.class);
		Authentication mockAuthentication = Mockito.mock(Authentication.class);

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", "testUser@gmail.com");
		attributes.put("sub", "google");

		when(mockPrincipal.getPrincipal()).thenReturn(mockOAuth2User);
		when(mockOAuth2User.getAttributes()).thenReturn(attributes);
		when(mockOAuth2User.getAttribute("name")).thenReturn("testUser");
		SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
		when(mockAuthentication.getPrincipal()).thenReturn(mockOAuth2User);

		String viewName = controller.welcome(mockPrincipal, mockModel);

		assertEquals("welcome", viewName);
		verify(mockModel).addAttribute("message", "Welcome testUser, you have chosen Google Authentication.");
	}

	@Test
	void testWelcomeFacebook() {
		LoginController controller = new LoginController();

		OAuth2AuthenticationToken mockPrincipal = Mockito.mock(OAuth2AuthenticationToken.class);
		Model mockModel = Mockito.mock(Model.class);
		OAuth2User mockOAuth2User = Mockito.mock(OAuth2User.class);
		Authentication mockAuthentication = Mockito.mock(Authentication.class);

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", "testUser@gmail.com");
		attributes.put("name", "testUser");

		when(mockPrincipal.getPrincipal()).thenReturn(mockOAuth2User);
		when(mockOAuth2User.getAttributes()).thenReturn(attributes);
		when(mockOAuth2User.getAttribute("name")).thenReturn("testUser");
		SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
		when(mockAuthentication.getPrincipal()).thenReturn(mockOAuth2User);

		String viewName = controller.welcome(mockPrincipal, mockModel);

		assertEquals("welcome", viewName);
		verify(mockModel).addAttribute("message", "Welcome testUser, you have chosen Facebook Authentication.");
	}

	@Test
	void testLoginView() {
		LoginController controller = new LoginController();
		String viewName = controller.login();
		assertEquals("login", viewName);
	}

	@Test
	void testLoginErrorView() {
		LoginController controller = new LoginController();
		String viewName = controller.loginError();
		assertEquals("login-error", viewName);
	}

	@Test
	void testOnAuthenticationFailure() throws IOException {

		CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler();
		HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
		AuthenticationException mockException = Mockito.mock(AuthenticationException.class);
		LoginAttemptRepository mockRepository = Mockito.mock(LoginAttemptRepository.class);
		CustomPasswordEncoder mockEncoder = Mockito.mock(CustomPasswordEncoder.class);

		ReflectionTestUtils.setField(handler, "loginAttemptRepository", mockRepository);
		ReflectionTestUtils.setField(handler, "customPasswordEncoder", mockEncoder);

		when(mockRequest.getParameter("username")).thenReturn("testUser");
		when(mockRequest.getParameter("password")).thenReturn("testPassword");
		when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");
		when(mockEncoder.encode("testPassword")).thenReturn("encodedPassword");

		handler.onAuthenticationFailure(mockRequest, mockResponse, mockException);

		ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
		verify(mockRepository).save(captor.capture());
		LoginAttempt savedAttempt = captor.getValue();
		assertEquals("testUser", savedAttempt.getUsername());
		assertEquals("127.0.0.1", savedAttempt.getIpAddress());
		assertEquals("encodedPassword", savedAttempt.getPassword());

		verify(mockResponse).sendRedirect("/login-error?username=testUser");
	}

}