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

#include "jerasure_jni.h"

#include <jerasure.h>
#include <jerasure/reed_sol.h>
#include <string.h>

static jintArray makeRow(JNIEnv *env, int *elements, jsize count) {
  jclass intClass = (*env)->FindClass(env, "java/lang/Integer");
  jintArray row = (*env)->NewObjectArray(env, count, intClass, 0);

  jsize index = 0;
  for (; index < count; ++index) {
    (*env)->SetObjectArrayElement(env, row, index, elements[index]);
  }
  return row;
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_createVandermondeMatrix(
      JNIEnv *env, jint k, jint m, jint w) {
  int *matrix = reed_sol_vandermonde_coding_matrix(k, m, w);

  int *elements = new int[k];
  memcpy(elements, matrix, sizeof(int) * k);
  jintArray row = makeRow(env, elements, k);
  jclass arrayClass = (*env)->GetObjectClass(env, row);

  jobjectArray result = (*env)->NewObjectArray(env, m, arrayClass, NULL);
  (*env)->SetObjectArrayElement(env, result, 0, row);

  int index = 1;
  for (; index < m; ++index) {
    memcpy(elements, matrix + index * sizeof(int) * k, sizeof(int) * k);
    row = makeRow(env, elements, k);
    (*env)->SetObjectArrayElement(env, result, index, row);
  }

  delete[] elements;
  return result;
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_encode(JNIEnv *env, jint k,
      jint m, jint w, jobjectArray matrix, jobjectArray data) {
}

JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_ReedSolomonCodec_decode(
    JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix,
    jintArray erasures, jobjectArray data, jobjectArray coding) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_createCauchyMatrix(
      JNIEnv *env, jint k, jint m, jint w) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_convertToBitMatrix(
      JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_encode(JNIEnv *env, jint k,
      jint m, jint w, jobjectArray matrix, jobjectArray data, jint packSize) {
}

JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_decode(
    JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix,
    jintArray erasures, jobjectArray data, jobjectArray coding, jint packSize) {
}
