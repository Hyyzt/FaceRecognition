#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>

#include <string>
#include <iostream>
#include "com_example_test_NativeUtil.h"

using namespace std;
using namespace cv;
extern "C" {
JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_Utils_NativeUtil_computeDescripors(
		JNIEnv *env, jclass thiz, jlong mGrayAddr, jlong mRgbaAddr, jlong mOutputAddr) {
	Mat* pMatGr=(Mat*)mGrayAddr;
	Mat* pMatRgb=(Mat*)mRgbaAddr;
	Mat* pMatDesc=(Mat*)mOutputAddr;
	vector<KeyPoint> v;

	//OrbFeatureDetector detector(50);
	OrbFeatureDetector detector;
	OrbDescriptorExtractor extractor;
	detector.detect(*pMatGr, v);

	extractor.compute(*pMatGr, v, *pMatDesc);
	//drawKeypoints(*pMatGr, v, *pMatDesc);

}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_Utils_NativeUtil_detectFeatures(
		JNIEnv *env, jclass thiz, jlong mGrayAddr, jlong mRgbaAddr, jlong mOutputAddr) {
	Mat* pMatGr=(Mat*)mGrayAddr;
	Mat* pMatRgb=(Mat*)mRgbaAddr;

	Mat* pMatOutput=(Mat*)mOutputAddr;
	vector<KeyPoint> v;

	//OrbFeatureDetector detector(50);
	OrbFeatureDetector detector;
	OrbDescriptorExtractor extractor;
	detector.detect(*pMatGr, v);

	drawKeypoints(*pMatGr, v, *pMatOutput);
}

JNIEXPORT jintArray JNICALL Java_org_opencv_samples_facedetect_Utils_NativeUtil_transformToGray(
		JNIEnv *env, jclass obj, jintArray pixels, jint width, jint height) {

	jboolean b;
	jint *buf;
	buf = env->GetIntArrayElements(pixels, &b);
	if (buf == NULL) {
		return 0;
	}

	//create the Mat and use your int array as input
	Mat imgData(height, width, CV_8UC4, (unsigned char*) buf);

	int size = width * height;
	jintArray result = env->NewIntArray(size);
	env->SetIntArrayRegion(result, 0, size, buf);
	env->ReleaseIntArrayElements(pixels, buf, 0);

	return result;
}

JNIEXPORT jstring JNICALL Java_org_opencv_samples_facedetect_Utils_NativeUtil_stringFromJNI(
		JNIEnv *env, jclass obj) {
	return env->NewStringUTF("Hello from JNI !");

}

}
