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

/**
 * Reed Solomon erasure codec, implemented with Cauchy matrix.
 */
public class CauchyReedSolomonCodec implements CodecInterface {

  private int dataBlockNum;
  private int codingBlockNum;
  private int wordSize;
  private int packetSize;
  private byte[][] cauchyBitMatrix;

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

    byte[][] matrix = createCauchyMatrix(dataBlockNum,
        codingBlockNum, wordSize);
    this.cauchyBitMatrix = convertToBitMatrix(dataBlockNum,
        codingBlockNum, wordSize, matrix);
  }

  /** {@inheritDoc} */
  @Override
  public byte[][] encode(byte[][] data) {
    return encode(dataBlockNum, codingBlockNum, wordSize,
        cauchyBitMatrix, data, packetSize);
  }

  /** {@inheritDoc} */
  @Override
  public void decode(int[] erasures, byte[][]data, byte[][] coding) {
    decode(dataBlockNum, codingBlockNum, wordSize, cauchyBitMatrix,
        erasures, data, coding, packetSize);
  }

  /**
   * Creates a Cauchy matrix over GF(2^w).
   *
   * @param k The column number
   * @param m The row number
   * @param w The word size, used to define the finite field
   * @return The generated Cauchy matrix
   */
  private native byte[][] createCauchyMatrix(int k, int m , int w);

  /**
   * Converts the Cauchy matrix to a bit matrix over GF(2^w).
   *
   * @param k The column number
   * @param m The row number
   * @param w The word size, used to define the finite field
   * @param matrix The cauchy matrix
   * @return The converted bit matrix
   */
  private native byte[][] convertToBitMatrix(int k, int m, int w,
      byte[][] matrix);

  /**
   * Encodes specified data blocks using given Cauchy matrix and packet size.
   *
   * @param k The number of data blocks
   * @param m The number of coding blocks
   * @param w The word size
   * @param matrix The cauchy bit matrix
   * @param data The data blocks matrix
   * @param packetSize The data packet size
   * @return The coding blocks matrix
   */
  private native byte[][] encode(int k, int m, int w, byte[][] matrix,
      byte[][] data, int packetSize);

  /**
   * Decodes specified failed data blocks using given Cauchy matrix, the
   * survivor data, coding blocks, and packet size.
   *
   * @param k The number of data blocks
   * @param m The number of coding blocks
   * @param w The word size
   * @param matrix The cauchy bit matrix
   * @param erasures The failed data blocks list
   * @param data The data blocks matrix
   * @param coding The coding blocks matrix
   * @param packetSize The data packet size
   */
  private native void decode(int k, int m, int w, byte[][] matrix,
      int[] erasures, byte[][] data, byte[][] coding, int packetSize);
}
