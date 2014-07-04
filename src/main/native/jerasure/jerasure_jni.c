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

#include "jerasure_jin.h"

#include <jerasure.h>

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_createVandermondeMatrix(
      jint k, jint m, jint w) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_ReedSolomonCodec_encode(jint k, jint m, jint w,
      jobjectArray matrix, jobjectArray data) {
}

JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_ReedSolomonCodec_decode(
    jint k, jint m, jint w, jobjectArray matrix, jintArray erasures,
    jobjectArray data, jobjectArray coding) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_createCauchyMatrix(
      jint k, jint m, jint w) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_convertToBitMatrix(
      jint k, jint m, jint w, jobjectArray matrix) {
}

JNIEXPORT jobjectArray JNICALL
  Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_encode(jint k, jint m,
      jint w, jobjectArray matrix, jobjectArray data, jint packSize) {
}

JNIEXPORT void JNICALL Java_com_xiaomi_infra_ec_CauchyReedSolomonCodec_decode(
    jint k, jint m, jint w, jobjectArray matrix, jintArray erasures,
    jobjectArray data, jobjectArray coding, jint packSize) {
}
