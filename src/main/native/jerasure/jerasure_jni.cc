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

#include <assert.h>
#include <jerasure.h>
#include <jerasure/cauchy.h>
#include <jerasure/reed_sol.h>
#include <string.h>

// Constructs a JNI intArray from a native int array
jintArray makeRow(JNIEnv *env, int *elements, jsize count) {
  jintArray row = env->NewIntArray(count);
  env->SetIntArrayRegion(row, 0, count, elements);
  return row;
}

// Convert a java byte[][] array to a native char[][] array
char **ConvertToNativeCharArray(JNIEnv *env, jobjectArray matrix) {
  jsize rowNum = env->GetArrayLength(matrix);
  char **result = new char*[rowNum];

  for (int row = 0; row < rowNum; ++row) {
    jbyteArray array = (jbyteArray)(env->GetObjectArrayElement(matrix, row));
    result[row] = (char *)(env->GetByteArrayElements(array, NULL));
  }
  return result;
}

// Convert a java int[][] array to a native int[] array
int *ConvertToNativeIntArray(JNIEnv *env, jobjectArray matrix) {
  jsize rowNum = env->GetArrayLength(matrix);
  jintArray array = (jintArray)(env->GetObjectArrayElement(matrix, 0));
  jsize columnNum = env->GetArrayLength(array);
  int *result = new int[rowNum * columnNum];

  for (int r = 0; r < rowNum; ++r) {
    array = (jintArray)(env->GetObjectArrayElement(matrix, r));
    jint* elements = env->GetIntArrayElements(array, NULL);
    memcpy(result + (r * columnNum), elements, columnNum * sizeof(int));
    env->ReleaseIntArrayElements(array, elements, 0);
  }
  return result;
}

// Convert a native char[][] array to a java byte[][] array
jobjectArray ConvertToJobjectArray(JNIEnv *env, char** matrix,
    int row, int column) {
  jclass arrayClass = env->FindClass("java/lang/Object");
  jobjectArray result = env->NewObjectArray(row, arrayClass, NULL);

  for (int r = 0; r < row; ++r) {
    jbyteArray array = env->NewByteArray(column);
    env->SetByteArrayRegion(array, 0, column, (jbyte*)matrix[r]);
    env->SetObjectArrayElement(result, r, array);
  }
  return result;
}

char **GetMatrix(int row, int column) {
  char **result = new char*[row];
  for (int r = 0; r < row; ++r) {
    result[r] = new char[column];
  }
  return result;
}

void ReleaseMatrix(char **matrix, int row) {
  for (int r = 0; r < row; ++r) {
    delete[] matrix[r];
  }
  delete[] matrix;
}

// Get the length of data
int GetDataLength(JNIEnv *env, jobjectArray data) {
  jsize length = env->GetArrayLength(data);
  assert(length > 0);

  jbyteArray array = (jbyteArray)(env->GetObjectArrayElement(data, 0));
  return (int)(env->GetArrayLength(array));
}

// Copy decode data back to origin data matrix
void CopyDecodedData(JNIEnv *env, jobjectArray data,
    jintArray erasures, char **decodedData) {
  jsize length = env->GetArrayLength(erasures);
  int* nativeErasures = env->GetIntArrayElements(erasures, NULL);

  for (int index = 0; index < length; ++index) {
    jbyteArray array = (jbyteArray)(env->GetObjectArrayElement(
          data, nativeErasures[index]));
    jsize data_len = env->GetArrayLength(array);
    env->SetByteArrayRegion(array, 0, data_len,
        (jbyte *)(decodedData[nativeErasures[index]]));
  }
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_createVandermondeMatrix(
      JNIEnv *env, jint k, jint m, jint w) {
  // Create the Vandermonde matrix using jerasure
  int *matrix = reed_sol_vandermonde_coding_matrix(k, m, w);

  // Convert the matrix to two dimensional java array
  int *elements = new int[k];
  memcpy(elements, matrix, sizeof(int) * k);
  jintArray row = makeRow(env, elements, k);
  jclass arrayClass = env->GetObjectClass(row);

  jobjectArray result = env->NewObjectArray(m, arrayClass, NULL);
  env->SetObjectArrayElement(result, 0, row);

  for (int index = 1; index < m; ++index) {
    memcpy(elements, matrix + index * sizeof(int) * k, sizeof(int) * k);
    row = makeRow(env, elements, k);
    env->SetObjectArrayElement(result, index, row);
  }

  delete[] elements;
  return result;
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_encode(JNIEnv *env, jint k,
      jint m, jint w, jobjectArray matrix, jobjectArray data) {
  int* nativeMatrix = ConvertToNativeIntArray(env, matrix);
  char** nativeData = ConvertToNativeCharArray(env, data);
  char** nativeCoding = GetMatrix(m, k);

  jerasure_matrix_encode(k, m, w, nativeMatrix, nativeData,
      nativeCoding, GetDataLength(env, data));
  jobjectArray result = ConvertToJobjectArray(env, nativeCoding, m, k);
  ReleaseMatrix(nativeCoding, m);
  return result;
}

JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_ReedSolomonCodec_decode(
    JNIEnv *env, jint k, jint m, jint w, jobjectArray matrix,
    jintArray erasures, jobjectArray data, jobjectArray coding) {
  int* nativeMatrix = ConvertToNativeIntArray(env, matrix);
  char** nativeData = ConvertToNativeCharArray(env, data);
  char** nativeCoding = ConvertToNativeCharArray(env, coding);
  int* nativeErasures = env->GetIntArrayElements(erasures, NULL);

  // Decode and copy back decoded data
  jerasure_matrix_decode(k, m, w, nativeMatrix, 1, nativeErasures,
      nativeData, nativeCoding, GetDataLength(env, data));
  CopyDecodedData(env, data, erasures, nativeData);
  env->ReleaseIntArrayElements(erasures, nativeErasures, 0);
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_createCauchyMatrix(
      JNIEnv *env, jint k, jint m, jint w) {
  int *matrix = cauchy_good_general_coding_matrix(k, m, w);
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
    jintArray erasures, jobjectArray data, jobjectArray coding,
    jint packSize) {
}
