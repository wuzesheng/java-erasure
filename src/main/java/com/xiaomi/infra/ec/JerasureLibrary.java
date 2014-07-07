/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.infra.ec;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * A simple JNA wrapper of Jerasure{@see https://bitbucket.org/jimplank/jerasure}.
 */
public interface JerasureLibrary extends Library {

  JerasureLibrary INSTANCE = (JerasureLibrary) Native.loadLibrary(
      "jerasure", JerasureLibrary.class);

  /**
   * Allocates and returns a m × k Vandermonde matrix in GF(2^w).
   */
  int[] reed_sol_vandermonde_conding_matrix(int k, int m, int w);

  /**
   * Allocates and returns a Cauchy matrix in GF(2^w).
   */
  int[] cauchy_good_general_coding_matrix(int k, int m, int w);

  /**
   * Converts a m × k matrix in GF(2^w) to a wm × wk bit-matrix.
   */
  int[] jerasure_matrix_to_bitmatrix(int k, int m, int w, int[] matrix);

  /**
   * Encodes with a specified matrix in GF(2^w). w must be ∈ {8, 16, 32}.
   *
   * @param k The number of data devices
   * @param m The number of coding devices
   * @param w The word size of the code
   * @param matrix An array with k*m elements that representing the coding matrix
   * @param data_ptrs An array of k pointers to size bytes worth of data. Each
   *                  of these must be long word aligned
   * @param coding_ptrs An array of m pointers to size bytes worth of coding
   *                    data. Each of these must be long word aligned
   * @param size The total number of bytes per device to encode
   */
  void jerasure_matrix_encode(int k, int m, int w, int[] matrix,
      byte[][] data_ptrs, byte[][] coding_ptrs, int size);

  /**
   * Decodes with a specified matrix in GF(2^w). w must be ∈ {8, 16, 32}.
   *
   * @param k The number of data devices
   * @param m The number of coding devices
   * @param w The word size of the code
   * @param matrix An array with k*m elements that representing the coding matrix
   * @param row_k_ones A number which should be one if the first row of matrix
   *                   is all ones
   * @param erasures An array of id’s of erased devices
   * @param data_ptrs An array of k pointers to size bytes worth of data. Each
   *                  of these must be long word aligned
   * @param coding_ptrs An array of m pointers to size bytes worth of coding
   *                    data. Each of these must be long word aligned
   * @param size The total number of bytes per device to encode
   * @return
   */
  int jerasure_matrix_decode(int k, int m, int w, int[] matrix,
      int row_k_ones, int[] erasures, byte[][] data_ptrs,
      byte[][] coding_ptrs, int size);

  /**
   * Encodes with a specified bit-matrix. Now w may be any number between 1
   * and 32.
   *
   * @param k The number of data devices
   * @param m The number of coding devices
   * @param w The word size of the code
   * @param bitmatrix An array with w*k*w*m elements that representing the
   *                  coding matrix
   * @param data_ptrs An array of k pointers to size bytes worth of data. Each
   *                  of these must be long word aligned.
   * @param coding_ptrs An array of m pointers to size bytes worth of coding
   *                    data. Each of these must be long word aligned.
   * @param size The total number of bytes per device to encode
   * @param packetsize The packet size
   */
  void jerasure_bitmatrix_encode(int k, int m, int w, int[] bitmatrix,
      byte[][] data_ptrs, byte[][] coding_ptrs, int size, int packetsize);

  /**
   * Decodes with a specified bit-matrix. Now w may be any number between 1
   * and 32.
   *
   * @param k The number of data devices
   * @param m The number of coding devices
   * @param w The word size of the code
   * @param bitmatrix An array with w*k*w*m elements that representing the
   *                  coding matrix
   * @param row_k_ones A number which should be one if the first row of matrix
   *                   is all ones
   * @param erasures An array of id’s of erased devices
   * @param data_ptrs An array of k pointers to size bytes worth of data. Each
   *                  of these must be long word aligned
   * @param coding_ptrs An array of m pointers to size bytes worth of coding
   *                    data. Each of these must be long word aligned
   * @param size The total number of bytes per device to encode
   * @param packetsize The packet size
   */
  int jerasure_bitmatrix_decode(int k, int m, int w, int[] bitmatrix,
      int row_k_ones, int[] erasures, byte[][] data_ptrs,
      byte[][] coding_ptrs, int size, int packetsize);
}
