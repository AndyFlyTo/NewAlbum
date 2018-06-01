/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.chenchen.newapplication.tensorflow;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Trace;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * A classifier specialized to label images using TensorFlow.
 */
public class TensorFlowImageClassifier implements Classifier {
    static {
        System.loadLibrary("tensorflow_inference");
        Log.d("chen", "libtensorflow_inference.so库加载成功");
    }

    private static final String TAG = "chen";


    // Config values.
    private String inputName;
    private String outputName;
    private int inputSize;
    private int imageMean;
    private float imageStd;

    // Pre-allocated buffers.
    private Vector<String> labels = new Vector<String>();  //string的数组
    private int[] intValues;
    private float[] floatValues;
    private float[] outputs;
    private String[] outputNames;

    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowImageClassifier() {
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     * @param inputSize     The input size. A square image of inputSize x inputSize is assumed.
     * @param imageMean     The assumed mean of the image values.
     * @param imageStd      The assumed std of the image values.
     * @param inputName     The label of the image input node.
     * @param outputName    The label of the output node.
     * @throws IOException
     */
    public static Classifier create(
            AssetManager assetManager,
            String modelFilename,
            String labelFilename,
            int inputSize,
            int imageMean,
            float imageStd,
            String inputName,
            String outputName) {
        TensorFlowImageClassifier c = new TensorFlowImageClassifier();
        c.inputName = inputName;
        c.outputName = outputName;


        String actualFilename = labelFilename.split("file:///android_asset/")[1];

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(assetManager.open(actualFilename)));
            String line;
            while ((line = br.readLine()) != null) {
                c.labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!", e);
        }

        c.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);

        // The shape of the output is [N, NUM_CLASSES], where N is the batch size.
        final Operation operation = c.inferenceInterface.graphOperation(outputName);
        final int numClasses = (int) operation.output(0).shape().size(1);


        c.inputSize = inputSize;
        c.imageMean = imageMean;
        c.imageStd = imageStd;

        c.outputNames = new String[]{outputName};
        c.intValues = new int[inputSize * inputSize];
        c.floatValues = new float[inputSize * inputSize * 3];
        c.outputs = new float[numClasses];

        return c;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public List<Recognition> recognizeImage(final Bitmap bitmap) {
   
        Trace.beginSection("recognizeImage");

        Trace.beginSection("preprocessBitmap");

        //getPixels()函数把一张图片，从指定的偏移位置（offset），指定的位置（x,y）截取指定的宽高（width,height ），把所得图像的每个像素颜色转为int值，存入pixels。
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        //这里no understand
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];

            floatValues[i * 3 + 0] = ((val >> 16) & 0xFF);
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF);
            floatValues[i * 3 + 2] = (val & 0xFF);
        }
        Trace.endSection();




        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 3);
        Log.d("chen", "feed");
        Trace.endSection();
        //inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SIZE, floatValues);
        // make the inference
        Trace.beginSection("run");
        inferenceInterface.run(new String[]{outputName});
        Trace.endSection();


        Arrays.fill(outputs, 0.0f);
        Trace.beginSection("fetch");
        inferenceInterface.fetch(outputName, outputs);
        Trace.endSection();

        int class_id = argmax(outputs);
        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        recognitions.add(new Recognition("" + class_id, labels.get(class_id), null, null));

        return recognitions;

    }


    public static int argmax(float[] elems) {

        int bestIdx = -1;
        float max = -1000;


        for (int i = 0; i < elems.length; i++) {
            Log.d("chen", elems[i] + "");
            float elem = elems[i];
            if (elem > max) {
                max = elem;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }
}
