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
package com.xiaomi.infra.ec.rs;

import com.google.common.base.Preconditions;

import com.xiaomi.infra.ec.CodecInterface;

/**
 * Normal Reed Solomon erasure codec, implemented with Vandermonde matrix.
 */
public class ReedSolomonCodec implements CodecInterface {

  private int dataBlockNum;
  private int codingBlockNum;
  private int wordSize;
  private int[][] vandermondeMatrix;

  public ReedSolomonCodec(int dataBlockNum, int codingBlockNum, int wordSize) {
    Preconditions.checkArgument(dataBlockNum > 0);
    Preconditions.checkArgument(codingBlockNum > 0);
    Preconditions.checkArgument(wordSize == 8 || wordSize == 16 ||
        wordSize == 32, "wordSize must be one of 8, 16 and 32");

    this.dataBlockNum = dataBlockNum;
    this.codingBlockNum = codingBlockNum;
    this.wordSize = wordSize;
    this.vandermondeMatrix = createVandermondeMatrix(this.dataBlockNum,
        this.codingBlockNum, this.wordSize);
  }

  /** {@inheritDoc} */
  @Override
  public byte[][] encode(byte[][] data) {
    return encode(dataBlockNum, codingBlockNum, wordSize,
        vandermondeMatrix, data);
  }

  /** {@inheritDoc} */
  @Override
  public void decode(int[] erasures, byte[][]data, byte[][] coding) {
    decode(dataBlockNum, codingBlockNum, wordSize, vandermondeMatrix,
        erasures, data, coding);
  }

  /**
   * Creates a Vandermonde matrix of m x k over GF(2^w).
   *
   * @param k The column number
   * @param m The row number
   * @param w The word size, used to define the finite field
   * @return The generated Vandermonde matrix
   */
  private native int[][] createVandermondeMatrix(int k, int m, int w);

  /**
   * Encodes specified data blocks using the given Vandermonde matrix.
   *
   * @param k The number of data blocks
   * @param m The number of coding blocks
   * @param w The word size
   * @param matrix The Vandermonde generate matrix of m x k
   * @param data The data blocks matrix
   * @return The coding blocks matrix
   */
  private native byte[][] encode(int k, int m, int w, int[][] matrix,
      byte[][] data);

  /**
   * Decodes specified failed data blocks using the given  Vandermonde matrix,
   * the survivor data and coding blocks.
   *
   * @param k The number of data blocks
   * @param m The number of coding blocks
   * @param w The word size
   * @param matrix The Vandermonde generate matrix of m x k
   * @param erasures The failed data blocks list
   * @param data The data blocks matrix
   * @param coding The coding blocks matrix
   */
  private native void decode(int k, int m, int w, int[][] matrix,
      int[] erasures, byte[][] data, byte[][] coding);
}
