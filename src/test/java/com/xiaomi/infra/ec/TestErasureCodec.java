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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.xiaomi.infra.ec.ErasureCodec.Algorithm;
import com.xiaomi.infra.ec.ErasureCodec.Builder;

public class TestErasureCodec {

  @Test
  public void TestReedSolomonCodec() {
    ErasureCodec codec = new Builder(Algorithm.Reed_Solomon)
        .dataBlockNum(6)
        .codingBlockNum(3)
        .wordSize(8)
        .build();
    runTest(codec, 6, 3, 32);
  }

  @Test
  public void TestCauchyReedSolomonCodec() {
    ErasureCodec codec = new Builder(Algorithm.Cauchy_Reed_Solomon)
        .dataBlockNum(6)
        .codingBlockNum(3)
        .wordSize(4)
        .packetSize(8)
        .build();
    runTest(codec, 6, 3, 32);
  }

  private void runTest(CodecInterface codec, int k, int m, int size) {
    Random random = new Random();
    // Generate data
    byte[][] data = new byte[k][size];
    byte[][] copiedData = new byte[k][size];
    for (int r = 0; r < data.length; ++r) {
      random.nextBytes(data[r]);
      System.arraycopy(data[r], 0, copiedData[r], 0, data[r].length);
    }
    System.out.println("Original data matrix:");
    CodecUtils.printMatrix(data);

    // Encode the data
    byte[][] coding = codec.encode(data);
    byte[][] copiedCoding = new byte[coding.length][coding[0].length];
    for (int r = 0; r < coding.length; ++r) {
      System.arraycopy(coding[r], 0, copiedCoding[r], 0, coding[r].length);
    }
    System.out.println("Original coding matrix:");
    CodecUtils.printMatrix(coding);

    // Erasure two random blocks
    int erasures[] = new int[m];
    Set<Integer> randomSet = new HashSet<Integer>();
    for (int i = 0; i < m; ++i) {
      int randomNum = random.nextInt(k + m);
      while (randomSet.contains(randomNum)) {
        randomNum = random.nextInt(k + m);
      }
      randomSet.add(randomNum);
      erasures[i] = randomNum;

      for (int c = 0; c < data[0].length; ++c) {
        if (erasures[i] < k) {
          data[erasures[i]][c] = 0;
        } else {
          coding[erasures[i] - k][c] = 0;
        }
      }
    }
    System.out.println("Erasures matrix:");
    CodecUtils.printMatrix(erasures, 1, erasures.length);
    System.out.println("Erasured data matrix:");
    CodecUtils.printMatrix(data);
    System.out.println("Erasured coding matrix:");
    CodecUtils.printMatrix(coding);

    // Decode data
    codec.decode(erasures, data, coding);
    System.out.println("Decoded data matrix:");
    CodecUtils.printMatrix(data);
    System.out.println("Decoded coding matrix:");
    CodecUtils.printMatrix(coding);

    // Check result
    Assert.assertArrayEquals(copiedData, data);
    Assert.assertArrayEquals(copiedCoding, coding);
  }
}
