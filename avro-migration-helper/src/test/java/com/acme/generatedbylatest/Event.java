/*
 * Copyright 2018 LinkedIn Corp.
 * Licensed under the BSD 2-Clause License (the "License"). 
 * See License in the project root for license information.
 */

package com.acme.generatedbylatest;
//this is code generated by the migration helper running under 1.8 with the compatibility option
@SuppressWarnings("all")
// @org.apache.avro.specific.AvroGenerated
public class Event extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"Event\",\"namespace\":\"com.acme\",\"fields\":[{\"name\":\"header\",\"type\":{\"type\":\"record\",\"name\":\"Header\",\"fields\":[{\"name\":\"intField\",\"type\":\"int\"},{\"name\":\"guid\",\"type\":{\"type\":\"fixed\",\"name\":\"Guid\",\"size\":16}}]}},{\"name\":\"enumField\",\"type\":{\"type\":\"enum\",\"name\":\"EnumType\",\"symbols\":[\"A\",\"B\",\"C\"]}},{\"name\":\"strField\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public Header header;
  public EnumType enumField;
  public CharSequence strField;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Event() {}

  /**
   * All-args constructor.
   */
  public Event(Header header, EnumType enumField, CharSequence strField) {
    this.header = header;
    this.enumField = enumField;
    this.strField = strField;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public Object get(int field$) {
    switch (field$) {
      case 0: return header;
      case 1: return enumField;
      case 2: return strField;
      default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
      case 0: header = (Header)value$; break;
      case 1: enumField = (EnumType)value$; break;
      case 2: strField = (CharSequence)value$; break;
      default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'header' field.
   */
  public Header getHeader() {
    return header;
  }

  /**
   * Sets the value of the 'header' field.
   * @param value the value to set.
   */
  public void setHeader(Header value) {
    this.header = value;
  }

  /**
   * Gets the value of the 'enumField' field.
   */
  public EnumType getEnumField() {
    return enumField;
  }

  /**
   * Sets the value of the 'enumField' field.
   * @param value the value to set.
   */
  public void setEnumField(EnumType value) {
    this.enumField = value;
  }

  /**
   * Gets the value of the 'strField' field.
   */
  public CharSequence getStrField() {
    return strField;
  }

  /**
   * Sets the value of the 'strField' field.
   * @param value the value to set.
   */
  public void setStrField(CharSequence value) {
    this.strField = value;
  }


}