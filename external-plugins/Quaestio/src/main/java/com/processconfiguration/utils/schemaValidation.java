/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package com.processconfiguration.utils;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class schemaValidation {

	public static void validate(URL schemaFile, File xmlFile) throws Exception {
		final String sl = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(sl);
		Schema schema = factory.newSchema(schemaFile);

		Validator v = schema.newValidator();
		v.validate(new StreamSource(xmlFile));
	}

}