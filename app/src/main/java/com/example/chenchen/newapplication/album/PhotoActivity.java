package com.example.chenchen.newapplication.album;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chenchen.newapplication.R;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/** 图像分类
 * Created by chenchen on 18-4-30.
 */

public class PhotoActivity extends AppCompatActivity {

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/frozen_modelCIFAR_new2.pb";
    private static final String TAG="chen";

    private static final String INPUT_NODE = "inputnode";
    private static final String OUTPUT_NODE = "outnode";

    private static final int[] INPUT_SIZE = {1,32,32,3};

    static {
        System.loadLibrary("tensorflow_inference");
        Log.d(TAG,"libtensorflow_inference.so库加载成功");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo);
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(),MODEL_FILE);
        Log.d(TAG,"model loaded successfully");
        //String imageUri = "drawable://" + R.drawable.models;

        AssetManager assetManager = getAssets();
        try {



            final int inputSize=32;

            final int destWidth = 32;
            final int destHeight = 32;


            // Load the image
            InputStream file = assetManager.open("ship.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(file);


            Bitmap bitmap_scaled = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);

            ImageView image= (ImageView) findViewById(R.id.car);
            image.setImageBitmap(bitmap);


            String[] classes = {"airplane","automobile","bird", "cat", "deer", "dog", "frog "," horse", "ship", "truck"};

            int[] intValues = new int[inputSize * inputSize]; // array to copy values from Bitmap image
            float[] floatValues = new float[inputSize * inputSize * 3]; // float array to store image data


            //get pixel values from bitmap image and store it in intValues
            bitmap_scaled.getPixels(intValues, 0, bitmap_scaled.getWidth(), 0, 0, bitmap_scaled.getWidth(), bitmap_scaled.getHeight());
            Log.d("chen","bitmap");
            for (int i = 0; i < intValues.length; ++i) {
                final int val = intValues[i];

                // convert from 0-255 range to floating point value
                floatValues[i * 3 + 0] = ((val >> 16) & 0xFF);
                floatValues[i * 3 + 1] = ((val >> 8) & 0xFF);
                floatValues[i * 3 + 2] = (val & 0xFF);
            }

            //inferenceInterface.feed(INPUT_NODE,INPUT_SIZE);
            Trace.beginSection("feed");
            inferenceInterface.feed(INPUT_NODE, floatValues, 1, inputSize, inputSize, 3);
            Log.d("chen","feed");
            Trace.endSection();
            //inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SIZE, floatValues);
            // make the inference
            Trace.beginSection("run");
            inferenceInterface.run(new String[] {OUTPUT_NODE});
            Trace.endSection();


            float [] result = new float[10];

            Arrays.fill(result,0.0f);
            Trace.beginSection("fetch");
            inferenceInterface.fetch(OUTPUT_NODE,result);
            Trace.endSection();
            ////inferenceInterface.readNodeFloat(OUTPUT_NODE, result);
            // 得到可能性最大的分类结果
            int class_id=argmax(result);

            TextView textView=(TextView) findViewById(R.id.result);
            textView.setText(classes[class_id]);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int argmax (float [] elems)
    {

        int bestIdx = -1;
        float max = -1000;
        for (int i = 0; i < elems.length; i++) {
            Log.d("chen",elems[i]+"");
            float elem = elems[i];
            if (elem > max) {
                max = elem;
                bestIdx = i;
            }
        }
        return bestIdx;
    }
}
