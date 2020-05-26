package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;

import static org.mockito.Mockito.*;

class BearerAuthenticationFilterTest {

    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    FilterChain emptyChain = mock(FilterChain.class);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    BearerAuthenticationFilter authFilter = new BearerAuthenticationFilter("/**");

    @BeforeEach
    void setUp() {
        authFilter.setAuthenticationManager(authenticationManager);
    }

    @Test
    void shouldNotAttemptAuthenticationOnMissingAuthorizationHeader() throws Exception {
        // given that a request does not contain an authorization header
        // when that request passes through the filter
        authFilter.doFilter(request, response, emptyChain);

        // then the filter does not attempt to authorize it
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldNotAttemptAuthenticationOnNonBearerAuthorizationHeader() throws Exception {
        // given that a request contains an authorization header that is not a Bearer token
        request.addHeader("Authorization", "Basic someBasicInfo");

        // when that request passes through the filter
        authFilter.doFilter(request, response, emptyChain);

        // then the filter does not attempt to authorize it
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldNotAttemptAuthenticationOnEmptyBearerToken() throws Exception {
        // given that a request contains an empty bearer token
        request.addHeader("Authorization", "Bearer ");

        // when that request passes through the filter
        authFilter.doFilter(request, response, emptyChain);

        // then the filter does not attempt to authorize it
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldNotAttemptToAuthenticateIfPathDoesntMatch() throws Exception {
        // given that a request is to a url which does not match the defaultFilterProcessUrl
        authFilter.setFilterProcessesUrl("/endpoint");
        request.setRequestURI("/differentendpoint");

        // when the request passes through the filter
        authFilter.doFilter(request, response, emptyChain);

        // the filter does not attempt authentication
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldAttemptAuthenticationOnNonEmptyBearerToken() throws Exception {
        // given that a request contains a non-empty bearer token
        request.addHeader("Authorization", "Bearer SomeBearerToken");

        // when that request passes through the filter
        authFilter.doFilter(request, response, emptyChain);

        // then the filter tries to authenticate it
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldContinueDownTheChainOnSuccessfulAuthentication() throws Exception {
        // given that a request contains valid authentication credentials
        request.addHeader("Authorization", "Bearer SomeBearerToken");
        Authentication responseAuth = new TestingAuthenticationToken("", "");
        when(authenticationManager.authenticate(any())).thenReturn(responseAuth);
        // when it passes through the filter
        authFilter.doFilter(request, response, emptyChain);

        // the filter continues down the chain after authentication
        InOrder inOrder = inOrder(authenticationManager, emptyChain);
        inOrder.verify(authenticationManager).authenticate(any());
        inOrder.verify(emptyChain).doFilter(any(), any());
    }
}