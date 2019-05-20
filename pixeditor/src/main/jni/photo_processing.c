/*
 * Copyright (C) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <stdlib.h>
#include <bitmap.h>
#include <mem_utils.h>
#include <android/log.h>
#include "beauty.h"

#define  LOG_TAG    "IMAGE_EDIT_PROCESSING"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static Bitmap bitmap;
//Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing
//com.fxn.pixeditor.imageeditengine
int Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeInitBitmap(JNIEnv* env, jobject thiz, jint width, jint height) {
	//System.out.print("hello error here");
	return initBitmapMemory(&bitmap, width, height);
}

//

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeGetBitmapRow(JNIEnv* env, jobject thiz, jint y, jintArray pixels) {
	int cpixels[bitmap.width];
	getBitmapRowAsIntegers(&bitmap, (int)y, &cpixels);
	(*env)->SetIntArrayRegion(env, pixels, 0, bitmap.width, cpixels);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeSetBitmapRow(JNIEnv* env, jobject thiz, jint y, jintArray pixels) {
	int cpixels[bitmap.width];
	(*env)->GetIntArrayRegion(env, pixels, 0, bitmap.width, cpixels);
	setBitmapRowFromIntegers(&bitmap, (int)y, &cpixels);
}

int Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeGetBitmapWidth(JNIEnv* env, jobject thiz) {
	return bitmap.width;
}

int Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeGetBitmapHeight(JNIEnv* env, jobject thiz) {
	return bitmap.height;
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeDeleteBitmap(JNIEnv* env, jobject thiz) {
	deleteBitmap(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeFlipHorizontally(JNIEnv* env, jobject thiz) {
	flipHorizontally(&bitmap, 1, 1, 1);
}

int Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeRotate90(JNIEnv* env, jobject thiz) {
	int resultCode = rotate90(&bitmap, 1, 1, 1);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}

	//All the component dimensions should have changed, so copy the correct dimensions
	bitmap.width = bitmap.redWidth;
	bitmap.height = bitmap.redHeight;
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeRotate180(JNIEnv* env, jobject thiz) {
	rotate180(&bitmap, 1, 1, 1);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyCustomFilter(JNIEnv* env, jobject thiz, jint y)  {
	applyCustomFilter(&bitmap,(int)y);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyInstafix(JNIEnv* env, jobject thiz)  {
	applyInstafix(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyAnsel(JNIEnv* env, jobject thiz) {
	applyAnselFilter(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyTestino(JNIEnv* env, jobject thiz) {
	applyTestino(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyXPro(JNIEnv* env, jobject thiz) {
	applyXPro(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyRetro(JNIEnv* env, jobject thiz) {
	applyRetro(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyBW(JNIEnv* env, jobject thiz) {
	applyBlackAndWhiteFilter(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplySepia(JNIEnv* env, jobject thiz) {
	applySepia(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyCyano(JNIEnv* env, jobject thiz) {
	applyCyano(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyGeorgia(JNIEnv* env, jobject thiz) {
	applyGeorgia(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplySahara(JNIEnv* env, jobject thiz) {
	applySahara(&bitmap);
}

void Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeApplyHDR(JNIEnv* env, jobject thiz) {
	applyHDR(&bitmap);
}

int Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeLoadResizedJpegBitmap(JNIEnv* env, jobject thiz, jbyteArray bytes, jint jpegSize, jint maxPixels) {
	char* jpegData = (char*) (*env)->GetPrimitiveArrayCritical(env, bytes, NULL);

	if (jpegData == NULL) {
		LOGE("jpeg data was null");
		return JNI_GET_INT_ARRAY_ERROR;
	}

	int resultCode = decodeJpegData(jpegData, jpegSize, maxPixels, &bitmap);
	if (resultCode != MEMORY_OK) {
		deleteBitmap(&bitmap);
		LOGE("error decoding jpeg resultCode=%d", resultCode);
		return resultCode;
	}

	(*env)->ReleasePrimitiveArrayCritical(env, bytes, jpegData, 0);

	return MEMORY_OK;
}

int Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_nativeResizeBitmap(JNIEnv* env, jobject thiz, jint newWidth, jint newHeight) {
	unsigned char* newRed;
	int resultCode = newUnsignedCharArray(newWidth*newHeight, &newRed);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resizeChannelBicubic(bitmap.red, bitmap.width, bitmap.height, newRed, (int)newWidth, (int)newHeight);
	freeUnsignedCharArray(&bitmap.red);
	bitmap.red = newRed;
	bitmap.redWidth = newWidth;
	bitmap.redHeight = newHeight;

	unsigned char* newGreen;
	resultCode = newUnsignedCharArray(newWidth*newHeight, &newGreen);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resizeChannelBicubic(bitmap.green, bitmap.width, bitmap.height, newGreen, (int)newWidth, (int)newHeight);
	freeUnsignedCharArray(&bitmap.green);
	bitmap.green = newGreen;
	bitmap.greenWidth = newWidth;
	bitmap.greenHeight = newHeight;

	unsigned char* newBlue;
	resultCode = newUnsignedCharArray(newWidth*newHeight, &newBlue);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resizeChannelBicubic(bitmap.blue, bitmap.width, bitmap.height, newBlue, (int)newWidth, (int)newHeight);
	freeUnsignedCharArray(&bitmap.blue);
	bitmap.blue = newBlue;
	bitmap.blueWidth = newWidth;
	bitmap.blueHeight = newHeight;

	bitmap.width = newWidth;
	bitmap.height = newHeight;
}



//------------------------ beauty module --------------------------------------
JNIEXPORT void JNICALL
Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_freeBeautifyMatrix(JNIEnv *env, jobject obj) {
    freeMatrix();
}

JNIEXPORT void JNICALL
Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_handleSmoothAndWhiteSkin(JNIEnv *env, jobject obj, jobject bitmap,
                                         jfloat smoothValue,jfloat whiteValue) {
    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if (bitmap == NULL)
        return;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGI("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGI("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    LOGI("Bitmap smooth and whiteskin handle");
    initBeautiMatrix((uint32_t *) pixels, info.width, info.height);

    LOGI("Bitmap smooth = %f and whiteSkin = %f", smoothValue,whiteValue);

    setSmooth((uint32_t *) pixels, smoothValue, info.width, info.height);
    setWhiteSkin((uint32_t *) pixels, whiteValue, info.width, info.height);

    AndroidBitmap_unlockPixels(env, bitmap);

    //free memory code
    freeMatrix();
}


JNIEXPORT void JNICALL
Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_handleSmooth(JNIEnv *env, jobject obj, jobject bitmap,
                                         jfloat smoothValue) {
    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if (bitmap == NULL)
        return;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGI("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGI("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    LOGI("AndroidBitmap_smooth handle");

    initBeautiMatrix((uint32_t *) pixels, info.width, info.height);
    setSmooth((uint32_t *) pixels, smoothValue, info.width, info.height);

    AndroidBitmap_unlockPixels(env, bitmap);

    //free memory code
    freeMatrix();
}

JNIEXPORT void JNICALL
Java_com_fxn_pixeditor_imageeditengine_filter_PhotoProcessing_handleWhiteSkin(JNIEnv *env, jobject obj, jobject bitmap,
                                            jfloat whiteValue) {
    AndroidBitmapInfo info;
    void *pixels;
    int ret;

    if (bitmap == NULL)
        return;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGI("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGI("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    LOGI("AndroidBitmap_whiteSkin handle");
    initBeautiMatrix((uint32_t *) pixels, info.width, info.height);

    LOGI("AndroidBitmap_whiteSkin whiteValue = %f", whiteValue);
    setWhiteSkin((uint32_t *) pixels, whiteValue, info.width, info.height);

    AndroidBitmap_unlockPixels(env, bitmap);

    //free memory
    freeMatrix();
}

void setWhiteSkin(uint32_t *pix, float whiteVal, int width, int height) {
    if (whiteVal >= 1.0 && whiteVal <= 10.0) { //1.0~10.0
        float a = log(whiteVal);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int offset = i * width + j;
                ARGB RGB;
                convertIntToArgb(mImageData_rgb[offset], &RGB);
                if (a != 0) {
                    RGB.red = 255 * (log(div255(RGB.red) * (whiteVal - 1) + 1) / a);
                    RGB.green = 255 * (log(div255(RGB.green) * (whiteVal - 1) + 1) / a);
                    RGB.blue = 255 * (log(div255(RGB.blue) * (whiteVal - 1) + 1) / a);
                }
                pix[offset] = convertArgbToInt(RGB);
            }
        }
    }//end if
}

void setSmooth(uint32_t *pix, float smoothValue, int width, int height) {//磨皮操作
    if (mIntegralMatrix == NULL || mIntegralMatrixSqr == NULL || mSkinMatrix == NULL) {//预操作辅助未准备好
        LOGE("not init correctly");
        return;
    }

    LOGE("AndroidBitmap_smooth setSmooth start---- smoothValue = %f", smoothValue);

    RGBToYCbCr((uint8_t *) mImageData_rgb, mImageData_yuv, width * height);

    int radius = width > height ? width * 0.02 : height * 0.02;

    for (int i = 1; i < height; i++) {
        for (int j = 1; j < width; j++) {
            int offset = i * width + j;
            if (mSkinMatrix[offset] == 255) {
                int iMax = i + radius >= height - 1 ? height - 1 : i + radius;
                int jMax = j + radius >= width - 1 ? width - 1 : j + radius;
                int iMin = i - radius <= 1 ? 1 : i - radius;
                int jMin = j - radius <= 1 ? 1 : j - radius;

                int squar = (iMax - iMin + 1) * (jMax - jMin + 1);
                int i4 = iMax * width + jMax;
                int i3 = (iMin - 1) * width + (jMin - 1);
                int i2 = iMax * width + (jMin - 1);
                int i1 = (iMin - 1) * width + jMax;

                float m = (mIntegralMatrix[i4]
                           + mIntegralMatrix[i3]
                           - mIntegralMatrix[i2]
                           - mIntegralMatrix[i1]) / squar;

                float v = (mIntegralMatrixSqr[i4]
                           + mIntegralMatrixSqr[i3]
                           - mIntegralMatrixSqr[i2]
                           - mIntegralMatrixSqr[i1]) / squar - m * m;
                float k = v / (v + smoothValue);

                mImageData_yuv[offset * 3] = ceil(m - k * m + k * mImageData_yuv[offset * 3]);
            }
        }
    }
    YCbCrToRGB(mImageData_yuv, (uint8_t *) pix, width * height);

    LOGI("AndroidBitmap_smooth setSmooth END!----");
}

void freeMatrix() {
    if (mIntegralMatrix != NULL) {        
		free(mIntegralMatrix);
        mIntegralMatrix = NULL;
    }

    if (mIntegralMatrixSqr != NULL) {
		free(mIntegralMatrixSqr);
        mIntegralMatrixSqr = NULL;
    }

    if (mSkinMatrix != NULL) {
		free(mSkinMatrix);
        mSkinMatrix = NULL;
    }

    if (mImageData_rgb != NULL) {
        free(mImageData_rgb);
        mImageData_rgb = NULL;
    }

    if (mImageData_yuv != NULL) {
		free(mImageData_yuv);
        mImageData_yuv = NULL;
    }
}

void initBeautiMatrix(uint32_t *pix, int width, int height) {
    if (mImageData_rgb == NULL)
		mImageData_rgb = (uint32_t *)malloc(sizeof(uint32_t)*width * height);

    memcpy(mImageData_rgb, pix, sizeof(uint32_t) * width * height);

    if (mImageData_yuv == NULL)
		mImageData_yuv = (uint8_t *)malloc(sizeof(uint8_t) * width * height * 3);
        
    RGBToYCbCr((uint8_t *) mImageData_rgb, mImageData_yuv, width * height);

    initSkinMatrix(pix, width, height);
    initIntegralMatrix(width, height);
}

void initSkinMatrix(uint32_t *pix, int w, int h) {
    LOGE("start - initSkinMatrix");
    if (mSkinMatrix == NULL)
		mSkinMatrix = (uint8_t *)malloc(sizeof(uint8_t) *w *h);
	//mSkinMatrix = new uint8_t[w * h];

    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            int offset = i * w + j;
            ARGB RGB;
            convertIntToArgb(pix[offset], &RGB);
            if ((RGB.blue > 95 && RGB.green > 40 && RGB.red > 20 &&
                 RGB.blue - RGB.red > 15 && RGB.blue - RGB.green > 15) ||//uniform illumination
                (RGB.blue > 200 && RGB.green > 210 && RGB.red > 170 &&
                 abs(RGB.blue - RGB.red) <= 15 && RGB.blue > RGB.red &&
                 RGB.green > RGB.red))//lateral illumination
                mSkinMatrix[offset] = 255;
            else
                mSkinMatrix[offset] = 0;
        }
    }
    LOGE("end - initSkinMatrix");
}

void initIntegralMatrix(int width, int height) {
    LOGI("initIntegral");
    LOGI("width = %d height = %d", width, height);

    if (mIntegralMatrix == NULL)
		mIntegralMatrix = (uint64_t *)malloc(sizeof(uint64_t) * width * height);
        //mIntegralMatrix = new uint64_t[width * height];
    if (mIntegralMatrixSqr == NULL)
		mIntegralMatrixSqr = (uint64_t *)malloc(sizeof(uint64_t) * width * height);
        //mIntegralMatrixSqr = new uint64_t[width * height];

    LOGI("malloc complete");

    //uint64_t *columnSum = new uint64_t[width];
	uint64_t *columnSum = (uint64_t *)malloc(sizeof(uint64_t) * width);
    //uint64_t *columnSumSqr = new uint64_t[width];
	uint64_t *columnSumSqr =(uint64_t *)malloc(sizeof(uint64_t) * width);

    columnSum[0] = mImageData_yuv[0];
    columnSumSqr[0] = mImageData_yuv[0] * mImageData_yuv[0];

    mIntegralMatrix[0] = columnSum[0];
    mIntegralMatrixSqr[0] = columnSumSqr[0];

    for (int i = 1; i < width; i++) {

        columnSum[i] = mImageData_yuv[3 * i];
        columnSumSqr[i] = mImageData_yuv[3 * i] * mImageData_yuv[3 * i];

        mIntegralMatrix[i] = columnSum[i];
        mIntegralMatrix[i] += mIntegralMatrix[i - 1];
        mIntegralMatrixSqr[i] = columnSumSqr[i];
        mIntegralMatrixSqr[i] += mIntegralMatrixSqr[i - 1];
    }

    for (int i = 1; i < height; i++) {
        int offset = i * width;

        columnSum[0] += mImageData_yuv[3 * offset];
        columnSumSqr[0] += mImageData_yuv[3 * offset] * mImageData_yuv[3 * offset];

        mIntegralMatrix[offset] = columnSum[0];
        mIntegralMatrixSqr[offset] = columnSumSqr[0];

        for (int j = 1; j < width; j++) {
            columnSum[j] += mImageData_yuv[3 * (offset + j)];
            columnSumSqr[j] += mImageData_yuv[3 * (offset + j)] * mImageData_yuv[3 * (offset + j)];

            mIntegralMatrix[offset + j] = mIntegralMatrix[offset + j - 1] + columnSum[j];
            mIntegralMatrixSqr[offset + j] = mIntegralMatrixSqr[offset + j - 1] + columnSumSqr[j];
        }
    }
	
	free(columnSum);
	free(columnSumSqr);
    //delete[] columnSum;
    //delete[] columnSumSqr;
    LOGI("initIntegral~end");
}

int32_t convertArgbToInt(ARGB argb)
{
    return (argb.alpha << 24) | (argb.red << 16) | (argb.green << 8) | argb.blue;
}

void YCbCrToRGB(uint8_t* From, uint8_t* To, int length)
{
    if (length < 1) return;
    int Red, Green, Blue;
    int Y, Cb, Cr;
    int i,offset;
    for(i = 0; i < length; i++)
    {
        offset = (i << 1) + i;
        Y = From[offset]; Cb = From[offset+1] - 128; Cr = From[offset+2] - 128;
        Red = Y + ((RGBRCrI * Cr + HalfShiftValue) >> Shift);
        Green = Y + ((RGBGCbI * Cb + RGBGCrI * Cr + HalfShiftValue) >> Shift);
        Blue = Y + ((RGBBCbI * Cb + HalfShiftValue) >> Shift);
        if (Red > 255) Red = 255; else if (Red < 0) Red = 0;
        if (Green > 255) Green = 255; else if (Green < 0) Green = 0;
        if (Blue > 255) Blue = 255; else if (Blue < 0) Blue = 0;
        offset = i << 2;

        To[offset] = (uint8_t)Blue;
        To[offset+1] = (uint8_t)Green;
        To[offset+2] = (uint8_t)Red;
        To[offset+3] = 0xff;
    }
}

void RGBToYCbCr(uint8_t* From, uint8_t* To, int length)
{
    if (length < 1) return;
    int Red, Green, Blue;
    int i,offset;
    for(i = 0; i < length; i++)
    {
        offset = i << 2;
        Blue = From[offset];
        Green = From[offset+1];
        Red = From[offset+2];

        offset = (i << 1) + i;
        To[offset] = (uint8_t)((YCbCrYRI * Red + YCbCrYGI * Green + YCbCrYBI * Blue + HalfShiftValue) >> Shift);
        To[offset+1] = (uint8_t)(128 + ((YCbCrCbRI * Red + YCbCrCbGI * Green + YCbCrCbBI * Blue + HalfShiftValue) >> Shift));
        To[offset+2] = (uint8_t)(128 + ((YCbCrCrRI * Red + YCbCrCrGI * Green + YCbCrCrBI * Blue + HalfShiftValue) >> Shift));
    }
}

void convertIntToArgb(uint32_t pixel, ARGB* argb) {
    argb->red = ((pixel >> 16) & 0xff);
    argb->green = ((pixel >> 8) & 0xff);
    argb->blue = (pixel & 0xff);
    argb->alpha = (pixel >> 24);
}

//------------------------ beauty module end --------------------------------------

