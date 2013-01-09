/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.security;

import com.sun.jndi.ldap.LdapCtxFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.Hashtable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class ADAuthenticationProvider implements AuthenticationProvider {
    private String url;
    private String pattern;
    private UserDetailsService userDetailsService;

    public ADAuthenticationProvider(String url, String pattern, UserDetailsService userDetailsService) {
        this.url = url;
        this.pattern = pattern;
        this.userDetailsService = userDetailsService;
    }

    private String getPrincipal(String uname) {
        MessageFormat mf = new MessageFormat(pattern);
        return mf.format(new String[]{uname});
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.getName() == null || (String) authentication.getCredentials() == null) {
            throw new BadCredentialsException("");
        }

        String principal = getPrincipal(authentication.getName());
        String passwd = (String) authentication.getCredentials();


        LdapContext ctx = null;
        try {
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, LdapCtxFactory.class.getCanonicalName());
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, principal);
            env.put(Context.SECURITY_CREDENTIALS, passwd);
            env.put(Context.PROVIDER_URL, url);
            ctx = new InitialLdapContext(env, null);
            //LDAP Connection Successful

            UserDetails userDetails = userDetailsService.loadUserByUsername(principal);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (NamingException nex) {
            throw new BadCredentialsException("LDAP authentication failed.", nex);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("UserDetails did not find a valid user for name: " + principal, e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
