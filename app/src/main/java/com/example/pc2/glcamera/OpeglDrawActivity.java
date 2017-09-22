package com.example.pc2.glcamera;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Created by pc on 2017/9/22.
 */

public class OpeglDrawActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testgl);
//        ((GlView)findViewById(R.id.glsurface)).setRender(new SanJiaoRender());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((GlView)findViewById(R.id.glsurface)).onResume();
    }
}
