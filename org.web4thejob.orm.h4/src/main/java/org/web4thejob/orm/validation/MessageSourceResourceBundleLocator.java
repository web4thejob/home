/*
 * Copyright (c) 2012-2014 Veniamin Isaias.
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

package org.web4thejob.orm.validation;

import org.hibernate.validator.resourceloading.ResourceBundleLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Custom class for supporting Hibernate Validator 5 (JSR 349) prior to the release of Spring 4+.
 *
 * @author Veniamin Isaias
 * @since 3.5.0
 */
public class MessageSourceResourceBundleLocator implements ResourceBundleLocator {

    /**
     * Build a MessageSourceResourceBundleLocator for the given MessageSource.
     *
     * @param messageSource the Spring MessageSource to wrap
     */
    public MessageSourceResourceBundleLocator(MessageSource messageSource) {
        Assert.notNull(messageSource, "MessageSource must not be null");
        this.messageSource = messageSource;
    }

    private final MessageSource messageSource;

    public ResourceBundle getResourceBundle(Locale locale) {
        return new MessageSourceResourceBundle(this.messageSource, locale);
    }

}
