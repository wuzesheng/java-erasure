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
package com.xiaomi.infra.ec.crs;

import com.google.common.base.Preconditions;

import com.xiaomi.infra.ec.CodecInterface;
import com.xiaomi.infra.ec.JerasureLibrary;

/**
 * Reed Solomon erasure codec, implemented with Cauchy matrix.
 */
public class CauchyReedSolomonCodec implements CodecInterface {

  private int dataBlockNum;
  private int codingBlockNum;
  private int wordSize;
  private int packetSize;
  private int[] cauchyBitMatrix;

  public CauchyReedSolomonCodec(int dataBlockNum, int codingBlockNum,
      int wordSize, int packetSize) {
    Preconditions.checkArgument(dataBlockNum > 0);
    Preconditions.checkArgument(codingBlockNum > 0);
    Preconditions.checkArgument(wordSize == 8 || wordSize == 16 ||
        wordSize == 32, "wordSize must be one of 8, 16 and 32");
    Preconditions.checkArgument(packetSize > 0);

    this.dataBlockNum = dataBlockNum;
    this.codingBlockNum = codingBlockNum;
    this.wordSize = wordSize;
    this.packetSize = packetSize;

    int[] matrix = createCauchyMatrix(dataBlockNum,
        codingBlockNum, wordSize);
    this.cauchyBitMatrix = convertToBitMatrix(dataBlockNum,
        codingBlockNum, wordSize, matrix);
  }

  /** {@inheritDoc} */
  @Override
  public byte[][] encode(byte[][] data) {
    Preconditions.checkArgument(data.length > 0);
    int size = data[0].length;
    byte[][] coding = new byte[codingBlockNum][size];
    JerasureLibrary.INSTANCE.jerasure_bitmatrix_encode(dataBlockNum,
        codingBlockNum, wordSize, cauchyBitMatrix, data, coding, size,
        packetSize);
    return coding;
  }

  /** {@inheritDoc} */
  @Override
  public void decode(int[] erasures, byte[][]data, byte[][] coding) {
    Preconditions.checkArgument(data.length > 0);
    int size = data[0].length;
    JerasureLibrary.INSTANCE.jerasure_bitmatrix_decode(dataBlockNum,
        codingBlockNum, wordSize, cauchyBitMatrix, 0, erasures,
        data, coding, size, packetSize);
  }

  /**
   * Creates a Cauchy matrix over GF(2^w).
   *
   * @param k The column number
   * @param m The row number
   * @param w The word size, used to define the finite field
   * @return The generated Cauchy matrix
   */
  int[] createCauchyMatrix(int k, int m , int w) {
    int[] matrix = JerasureLibrary.INSTANCE
        .cauchy_good_general_coding_matrix(k, m, w);
    return matrix;
  }

  /**
   * Converts the Cauchy matrix to a bit matrix over GF(2^w).
   *
   * @param k The column number
   * @param m The row number
   * @param w The word size, used to define the finite field
   * @param matrix The cauchy matrix
   * @return The converted bit matrix
   */
  int[] convertToBitMatrix(int k, int m, int w, int[] matrix) {
    int[] bit_matrix = JerasureLibrary.INSTANCE.jerasure_matrix_to_bitmatrix(
        k, m, w, matrix);
    return bit_matrix;
  }
}
