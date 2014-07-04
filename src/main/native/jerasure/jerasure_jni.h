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

#ifndef JAVA_ERASURE_JERASURE_JNI_H_
#define JAVA_ERASURE_JERASURE_JNI_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

/**
 * Creates a Vandermonde matrix of m x k over GF(2^w).
 *
 * @param env The JNI environment pointer
 * @param k The column number
 * @param m The row number
 * @param w The word size, used to define the finite field
 * @return The generated Vandermonde matrix
 */
JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_createVandermondeMatrix(
      JNIEnv *env, jint k, jint m, jint w);

/**
 * Encodes specified data blocks using the given Vandermonde matrix.
 *
 * @param env The JNI environment pointer
 * @param k The number of data blocks
 * @param m The number of coding blocks
 * @param w The word size
 * @param matrix The Vandermonde generate matrix of m x k
 * @param data The data blocks matrix
 * @return The coding blocks matrix
 */
JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_encode(JNIEnv *env, jint k, jint m,
      jint w, jobjectArray matrix, jobjectArray data);

/**
 * Decodes specified failed data blocks using the given  Vandermonde matrix,
 * the survivor data and coding blocks.
 *
 * @param env The JNI environment pointer
 * @param k The number of data blocks
 * @param m The number of coding blocks
 * @param w The word size
 * @param matrix The Vandermonde generate matrix of m x k
 * @param erasures The failed data blocks list
 * @param data The data blocks matrix
 * @param coding The coding blocks matrix
 */
JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_ReedSolomonCodec_decode(
    JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix,
    jintArray erasures, jobjectArray data, jobjectArray coding);

/**
 * Creates a Cauchy matrix over GF(2^w).
 *
 * @param env The JNI environment pointer
 * @param k The column number
 * @param m The row number
 * @param w The word size, used to define the finite field
 * @return The generated Cauchy matrix
 */
JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_createCauchyMatrix(
      JNIEnv *env, jint k, jint m, jint w);

/**
 * Converts the Cauchy matrix to a bit matrix over GF(2^w).
 *
 * @param env The JNI environment pointer
 * @param k The column number
 * @param m The row number
 * @param w The word size, used to define the finite field
 * @param matrix The cauchy matrix
 * @return The converted bit matrix
 */
JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_convertToBitMatrix(
      JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix);

/**
 * Encodes specified data blocks using given Cauchy matrix and packet size.
 *
 * @param env The JNI environment pointer
 * @param k The number of data blocks
 * @param m The number of coding blocks
 * @param w The word size
 * @param matrix The cauchy bit matrix
 * @param data The data blocks matrix
 * @param packetSize The data packet size
 * @return The coding blocks matrix
 */
JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_encode(JNIEnv *env, jint k,
      jint m, jint w, jobjectArray matrix, jobjectArray data, jint packSize);

/**
 * Decodes specified failed data blocks using given Cauchy matrix, the
 * survivor data, coding blocks, and packet size.
 *
 * @param env The JNI environment pointer
 * @param k The number of data blocks
 * @param m The number of coding blocks
 * @param w The word size
 * @param matrix The cauchy bit matrix
 * @param erasures The failed data blocks list
 * @param data The data blocks matrix
 * @param coding The coding blocks matrix
 * @param packetSize The data packet size
 */
JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_decode(
    JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix,
    jintArray erasures, jobjectArray data, jobjectArray coding, jint packSize);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif // JAVA_ERASURE_JERASURE_JNI_H_
