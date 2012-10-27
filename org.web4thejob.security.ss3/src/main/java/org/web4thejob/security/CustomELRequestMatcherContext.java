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

import org.springframework.security.web.util.IpAddressMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Veniamin Isaias
 * @since 1.1.1
 */

public class CustomELRequestMatcherContext {
    private final HttpServletRequest request;

    public CustomELRequestMatcherContext(HttpServletRequest request) {
        this.request = request;
    }

    public boolean hasIpAddress(String ipAddress) {
        return (new IpAddressMatcher(ipAddress).matches(request));
    }

    public boolean hasHeader(String headerName, String value) {
        String header = request.getHeader(headerName);
        if (!StringUtils.hasText(header)) {
            return false;
        }

        return header.contains(value);

    }

    public boolean isFromIntranet() {
        return SecurityUtil.isFromIntranet(request.getRemoteAddr());
    }

    public boolean isFirstUse() {
        return SecurityUtil.isFirstUse();
    }
}
