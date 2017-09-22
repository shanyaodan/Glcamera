package com.example.pc2.glcamera;

import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pc on 2017/9/22.
 */

public class MyRender implements GLSurfaceView.Renderer , SurfaceTexture.OnFrameAvailableListener {
    private int mTextureID;
    private SurfaceTexture mSurface ;
    private CameraHelper cameraHelper;
    private GLSurfaceView mGLSurfaceView;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 inputTextureCoordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{"+
                    "gl_Position = vPosition;"+
                    "textureCoordinate = inputTextureCoordinate;" +
                    "}";

    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n"+
                    "precision mediump float;" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES s_texture;\n" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
                    "}";

    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private  int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    static float squareCoords[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f,  1.0f,
    };

    static float textureVertices[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    public  MyRender(CameraHelper cameraHelper,GLSurfaceView mGLSurfaceView){
        this.cameraHelper =  cameraHelper;
        this.mGLSurfaceView = mGLSurfaceView;

    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        cameraHelper.openCamera();

        initGLES20();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
       GLES20.glViewport(0,0,i,i1);
        try {
            cameraHelper.mCamera.setPreviewTexture(mSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initCamera(1.33f);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        drawGLES20();
        mSurface.updateTexImage();
//        float[] mtx = new float[16];
//        mSurface.getTransformMatrix(mtx);

    }

    private int createTextureID()
    {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
    private  int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    public void initGLES20(){



        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader    = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader  = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
    }

    public void drawGLES20(){
        GLES20.glUseProgram(mProgram);
        //选择当前活跃的纹理源
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将生成的纹理的名称绑定到指定的纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

//        textureVerticesBuffer.clear();
//        textureVerticesBuffer.put( transformTextureCoordinates( textureVertices, mtx ));
//        textureVerticesBuffer.position(0);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);

    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLSurfaceView.requestRender();
    }


    private void initCamera(float previewRate){
        if(cameraHelper.mCamera != null){

            Camera.Parameters mParams = cameraHelper.mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
//			CamParaUtil.getInstance().printSupportPictureSize(mParams);
//			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            //设置PreviewSize和PictureSize
            Camera.Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(),previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Camera.Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            cameraHelper.mCamera.setDisplayOrientation(90);

//			CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
//			if(focusModes.contains("continuous-video")){
//				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//			}
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)){
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            }
            mParams.setAutoWhiteBalanceLock(true);
            mParams.set("iso","ISO800");
            cameraHelper.mCamera.setParameters(mParams);
            cameraHelper.mCamera.startPreview();//开启预览

//            isPreviewing = true;
//            mPreviwRate = previewRate;

//            mParams = cameraHelper.mCamera.getParameters(); //重新get一次
//            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
//                    + "Height = " + mParams.getPreviewSize().height);
//            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
//                    + "Height = " + mParams.getPictureSize().height);
        }
    }


}
