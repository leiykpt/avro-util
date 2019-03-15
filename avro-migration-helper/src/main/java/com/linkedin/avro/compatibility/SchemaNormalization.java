/*
 * Copyright 2018 LinkedIn Corp.
 * Licensed under the BSD 2-Clause License (the "License"). 
 * See License in the project root for license information.
 */
package com.linkedin.avro.compatibility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;


/**
 * this class has been back-ported from modern (1.8.2) avro to allow canonicalizing schemas
 * under avro 1.4. its only intended for use by migration helper code (hence its package private)
 */
class SchemaNormalization {
  private SchemaNormalization() {
  }

  /** Returns "Parsing Canonical Form" of a schema as defined by Avro
   * spec. */
  public static String toParsingForm(Schema s) {
    try {
      Map<String, String> env = new HashMap<>();
      return build(env, s, new StringBuilder()).toString();
    } catch (IOException e) {
      // Shouldn't happen, b/c StringBuilder can't throw IOException
      throw new RuntimeException(e);
    }
  }

  /** Returns a fingerprint of a string of bytes.  This string is
   * presumed to contain a canonical form of a schema.  The
   * algorithm used to compute the fingerprint is selected by the
   * argument <i>fpName</i>.  If <i>fpName</i> equals the string
   * <code>"CRC-64-AVRO"</code>, then the result of {@link #fingerprint64} is
   * returned in little-endian format.  Otherwise, <i>fpName</i> is
   * used as an algorithm name for {@link
   * MessageDigest#getInstance(String)}, which will throw
   * <code>NoSuchAlgorithmException</code> if it doesn't recognize
   * the name.
   * <p> Recommended Avro practice dictates that
   * <code>"CRC-64-AVRO"</code> is used for 64-bit fingerprints,
   * <code>"MD5"</code> is used for 128-bit fingerprints, and
   * <code>"SHA-256"</code> is used for 256-bit fingerprints. */
  public static byte[] fingerprint(String fpName, byte[] data) throws NoSuchAlgorithmException {
    if (fpName.equals("CRC-64-AVRO")) {
      long fp = fingerprint64(data);
      byte[] result = new byte[8];
      for (int i = 0; i < 8; i++) {
        result[i] = (byte) fp;
        fp >>= 8;
      }
      return result;
    }

    MessageDigest md = MessageDigest.getInstance(fpName);
    return md.digest(data);
  }

  /** Returns the 64-bit Rabin Fingerprint (as recommended in the Avro
   * spec) of a byte string. */
  public static long fingerprint64(byte[] data) {
    long result = EMPTY64;
    for (byte b : data) {
      result = (result >>> 8) ^ FP64.FP_TABLE[(int) (result ^ b) & 0xff];
    }
    return result;
  }

  /** Returns {@link #fingerprint} applied to the parsing canonical form
   * of the supplied schema. */
  public static byte[] parsingFingerprint(String fpName, Schema s) throws NoSuchAlgorithmException {
    try {
      return fingerprint(fpName, toParsingForm(s).getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /** Returns {@link #fingerprint64} applied to the parsing canonical form
   * of the supplied schema. */
  public static long parsingFingerprint64(Schema s) {
    try {
      return fingerprint64(toParsingForm(s).getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static Appendable build(Map<String, String> env, Schema s, Appendable o) throws IOException {
    boolean firstTime = true;
    Schema.Type st = s.getType();
    switch (st) {

      case UNION:
        o.append('[');
        for (Schema b : s.getTypes()) {
          if (!firstTime) {
            o.append(',');
          } else {
            firstTime = false;
          }
          build(env, b, o);
        }
        return o.append(']');

      case ARRAY:
      case MAP:
        o.append("{\"type\":\"").append(st.name()).append("\"");
        if (st == Schema.Type.ARRAY) {
          build(env, s.getElementType(), o.append(",\"items\":"));
        } else {
          build(env, s.getValueType(), o.append(",\"values\":"));
        }
        return o.append("}");

      case ENUM:
      case FIXED:
      case RECORD:
        String name = s.getFullName();
        if (env.get(name) != null) {
          return o.append(env.get(name));
        }
        String qname = "\"" + name + "\"";
        env.put(name, qname);
        o.append("{\"name\":").append(qname);
        o.append(",\"type\":\"").append(st.name()).append("\"");
        if (st == Schema.Type.ENUM) {
          o.append(",\"symbols\":[");
          for (String enumSymbol : s.getEnumSymbols()) {
            if (!firstTime) {
              o.append(',');
            } else {
              firstTime = false;
            }
            o.append('"').append(enumSymbol).append('"');
          }
          o.append("]");
        } else if (st == Schema.Type.FIXED) {
          o.append(",\"size\":").append(Integer.toString(s.getFixedSize()));
        } else { // st == Schema.Type.RECORD
          o.append(",\"fields\":[");
          for (Schema.Field f : s.getFields()) {
            if (!firstTime) {
              o.append(',');
            } else {
              firstTime = false;
            }
            o.append("{\"name\":\"").append(f.name()).append("\"");
            build(env, f.schema(), o.append(",\"type\":")).append("}");
          }
          o.append("]");
        }
        return o.append("}");

      default: // boolean, bytes, double, float, int, long, null, string
        return o.append('"').append(st.name()).append('"');
    }
  }

  final static long EMPTY64 = 0xc15d213aa4d7a795L;

  /* An inner class ensures that FP_TABLE initialized only when needed. */
  private static class FP64 {
    private static final long[] FP_TABLE = new long[256];

    static {
      for (int i = 0; i < 256; i++) {
        long fp = i;
        for (int j = 0; j < 8; j++) {
          long mask = -(fp & 1L);
          fp = (fp >>> 1) ^ (EMPTY64 & mask);
        }
        FP_TABLE[i] = fp;
      }
    }
  }
}
