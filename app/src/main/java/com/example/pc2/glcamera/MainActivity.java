package com.example.pc2.glcamera;

import android.Manifest;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements Camera.PreviewCallback {

    GLSurfaceView mGlsurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                123);

        mGlsurface = (GLSurfaceView) findViewById(R.id.glsurface);
        mGlsurface.setEGLContextClientVersion(2);

        CameraHelper cameraHelper= new CameraHelper();
        cameraHelper.openCamera();
        cameraHelper.mCamera.setPreviewCallback(this);

        mGlsurface.setRenderer(new MyRender(cameraHelper,mGlsurface));
        mGlsurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }


}
