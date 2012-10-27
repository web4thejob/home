/*
 * Copyright (c) 2012 Veniamin Isaias.
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

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */


public class CredentialsExpiredErrorHandler extends SimpleUrlAuthenticationFailureHandler {

    private String passwordChangeUrl;
    private String expiredUserName;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof CredentialsExpiredException && passwordChangeUrl != null && expiredUserName != null) {
            saveException(request, exception);
            logger.debug("Created new " + CredentialsRenewAuthenticationToken.class.getSimpleName());
            SecurityContextHolder.getContext().setAuthentication(new CredentialsRenewAuthenticationToken
                    (expiredUserName));

            logger.debug("Forwarding to " + passwordChangeUrl);
            getRedirectStrategy().sendRedirect(request, response, passwordChangeUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    public String getPasswordChangeUrl() {
        return passwordChangeUrl;
    }

    public void setPasswordChangeUrl(String passwordChangeUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(passwordChangeUrl), "'" + passwordChangeUrl + "' is not a valid " +
                "redirect URL");
        this.passwordChangeUrl = passwordChangeUrl;
    }

    public void setExpiredUserName(String expiredUserName) {
        this.expiredUserName = expiredUserName;
    }
}
