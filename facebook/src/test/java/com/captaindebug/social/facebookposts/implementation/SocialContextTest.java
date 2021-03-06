/**
 * Copyright 2012 Marin Solutions
 */
package com.captaindebug.social.facebookposts.implementation;

import static org.easymock.EasyMock.expect;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;

/**
 * @author Roger
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SocialContextTest {

	private SocialContext instance;

	@Mock
	private UsersConnectionRepository userConnectionRespository;

	@Mock
	private ConnectionRepository connectionRespository;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Facebook facebook;

	// Use the real thing as this is lightweight;
	private UserCookieGenerator userCookieGenerator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		userCookieGenerator = new UserCookieGenerator();
		instance = new SocialContext(userConnectionRespository, userCookieGenerator, facebook);
	}

	@Test
	public void testPreHandleWithNoExistingCookieAvailable() throws Exception {

		expect(request.getCookies()).andReturn(null);

		replay();
		instance.isSignedIn(request, response);
		verify();
	}

	@Test
	public void testPreHandleWithDisconnectedUser() throws Exception {

		final String COOKIE_NAME = "captain_debug_social_user";
		final String COOKIE_VALUE = "qwerty";
		Cookie[] cookies = new Cookie[1];
		Cookie cookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);
		cookies[0] = cookie;

		expect(request.getCookies()).andReturn(cookies);

		expect(userConnectionRespository.createConnectionRepository(COOKIE_VALUE)).andReturn(connectionRespository);
		expect(connectionRespository.findPrimaryConnection(Facebook.class)).andReturn(null);

		cookie = new Cookie(COOKIE_NAME, "");
		response.addCookie(cookie);

		replay();
		instance.isSignedIn(request, response);
		verify();

	}
}
