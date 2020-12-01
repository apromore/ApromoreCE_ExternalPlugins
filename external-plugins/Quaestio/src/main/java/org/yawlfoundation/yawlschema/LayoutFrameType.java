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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.03 at 05:04:23 PM CET 
//

package org.yawlfoundation.yawlschema;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for LayoutFrameType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="LayoutFrameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="w" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="h" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayoutFrameType")
public class LayoutFrameType {

	@XmlAttribute(required = true)
	protected BigInteger x;
	@XmlAttribute(required = true)
	protected BigInteger y;
	@XmlAttribute(required = true)
	protected BigInteger w;
	@XmlAttribute(required = true)
	protected BigInteger h;

	/**
	 * Gets the value of the x property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getX() {
		return x;
	}

	/**
	 * Sets the value of the x property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setX(BigInteger value) {
		this.x = value;
	}

	/**
	 * Gets the value of the y property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getY() {
		return y;
	}

	/**
	 * Sets the value of the y property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setY(BigInteger value) {
		this.y = value;
	}

	/**
	 * Gets the value of the w property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getW() {
		return w;
	}

	/**
	 * Sets the value of the w property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setW(BigInteger value) {
		this.w = value;
	}

	/**
	 * Gets the value of the h property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getH() {
		return h;
	}

	/**
	 * Sets the value of the h property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setH(BigInteger value) {
		this.h = value;
	}

}
