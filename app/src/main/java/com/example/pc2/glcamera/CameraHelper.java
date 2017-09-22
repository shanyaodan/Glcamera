package com.example.pc2.glcamera;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by pc on 2017/9/22.
 */

public class CameraHelper {
    public Camera mCamera;
    public void openCamera(){
        mCamera = Camera.open();
//        initCamera();
    }

//    private void initCamera(float previewRate){
//        if(mCamera != null){
//
//            mParams = mCamera.getParameters();
//            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
////			CamParaUtil.getInstance().printSupportPictureSize(mParams);
////			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
//            //设置PreviewSize和PictureSize
//            Camera.Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
//                    mParams.getSupportedPictureSizes(),previewRate, 800);
//            mParams.setPictureSize(pictureSize.width, pictureSize.height);
//            Camera.Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
//                    mParams.getSupportedPreviewSizes(), previewRate, 800);
//            mParams.setPreviewSize(previewSize.width, previewSize.height);
//
//            mCamera.setDisplayOrientation(90);
//
////			CamParaUtil.getInstance().printSupportFocusMode(mParams);
//            List<String> focusModes = mParams.getSupportedFocusModes();
////			if(focusModes.contains("continuous-video")){
////				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
////			}
//            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)){
//                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
//            }
//            mParams.setAutoWhiteBalanceLock(true);
//            mParams.set("iso","ISO800");
//            mCamera.setParameters(mParams);
//            mCamera.startPreview();//开启预览
//
//            isPreviewing = true;
//            mPreviwRate = previewRate;
//
//            mParams = mCamera.getParameters(); //重新get一次
//            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
//                    + "Height = " + mParams.getPreviewSize().height);
//            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
//                    + "Height = " + mParams.getPictureSize().height);
//        }
//    }


}
