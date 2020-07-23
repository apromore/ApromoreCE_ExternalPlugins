/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.08.01 at 05:03:55 PM EST 
//


package com.processconfiguration.qml;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FactType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FactType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="mandatory" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="guidelines" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="impact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="partiallyDepends" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fullyDepends" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FactType", propOrder = {
    "description",
    "_default",
    "mandatory",
    "guidelines",
    "impact",
    "preFL"
})
public class FactType {

    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "default")
    protected Boolean _default;
    @XmlElement(defaultValue = "false")
    protected Boolean mandatory;
    protected String guidelines;
    protected String impact;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute
    protected String partiallyDepends;
    @XmlAttribute
    protected String fullyDepends;
    
    //added
    protected ArrayList<ArrayList<String>> preFL;   

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefault() {
        if (_default!=null)
        	return _default;//although the default value is "false", somehow this is not enforced by JAXB
        else
        	return false;
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDefault(Boolean value) {
        this._default = value;
    }

    /**
     * Gets the value of the mandatory property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMandatory() {
        if (mandatory!=null)
        	return mandatory;
        else
        	 return false;
    }

    /**
     * Sets the value of the mandatory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMandatory(Boolean value) {
        this.mandatory = value;
    }

    /**
     * Gets the value of the guidelines property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidelines() {
        return guidelines;
    }

    /**
     * Sets the value of the guidelines property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidelines(String value) {
        this.guidelines = value;
    }

    /**
     * Gets the value of the impact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImpact() {
        return impact;
    }

    /**
     * Sets the value of the impact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImpact(String value) {
        this.impact = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the partiallyDepends property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartiallyDepends() {
        return partiallyDepends;
    }

    /**
     * Sets the value of the partiallyDepends property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartiallyDepends(String value) {
        this.partiallyDepends = value;
    }

    /**
     * Gets the value of the fullyDepends property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullyDepends() {
        return fullyDepends;
    }

    /**
     * Sets the value of the fullyDepends property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullyDepends(String value) {
        this.fullyDepends = value;
    }
    
    //added: WARNING - can only be invoked once this.partiallyDepends and this.fullyDepends have been initialised
    public void setPreFL(){
    	List<String> partiallyDependsL = new ArrayList<String>();
      	List<String> fullyDependsL = new ArrayList<String>();
      	ArrayList<String> tempP = new ArrayList<String>();
    	this.preFL = new ArrayList<ArrayList<String>>();
    	StringTokenizer st;
    	
    	if (this.fullyDepends!=null){
    		st =  new StringTokenizer(this.fullyDepends, " ");  
       		while(st.hasMoreTokens()){
       			fullyDependsL.add(st.nextToken().substring(1));
       		}
    	}
    	
    	if (this.partiallyDepends!=null){
    		st = new StringTokenizer(this.partiallyDepends, " ");  
    		while(st.hasMoreTokens()){
    			partiallyDependsL.add(st.nextToken().substring(1));
       		}
    		for (String pd : partiallyDependsL) {
    			tempP = new ArrayList<String>(fullyDependsL);//this tempP needs to be refreshed for every cycle of the for
    			tempP.add(pd);
    			preFL.add(tempP);
    			//tempP.clear();//this is NOT needed, otherwise it doesn't work as it adds a pointer to a list, rather than copying the element as new
    		}   	
    	}
    	else if (this.fullyDepends!=null){
    		preFL.add((ArrayList<String>) fullyDependsL);//preFL is only made of one element containing the full dependencies
    	}
    }
    
    //added
    public ArrayList<ArrayList<String>> getPreFL(){
    	return this.preFL;
    }
    
    //added
	public String toString(){
		return getDescription();
	}

}
