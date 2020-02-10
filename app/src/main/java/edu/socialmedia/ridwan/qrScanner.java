package edu.socialmedia.ridwan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class qrScanner extends AppCompatActivity {



    CameraView camera_view;
    boolean isDetected = false;
    Button btn_start_again;

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector dectector;
    private AtomicBoolean mShouldThrottle;
    private FirebaseVisionImageMetadata mMetadata;
    qrScanner mQrScanner = new qrScanner();
    private CameraSource mCameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        Dexter.withActivity(this)
               .withPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        setupCamera();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

    }

    private void setupCamera() {
        btn_start_again = (Button) findViewById(R.id.btn_again);
        btn_start_again.setEnabled(isDetected);
        btn_start_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
               btn_start_again.setEnabled(isDetected);
            }
        });
       BarcodeDetector mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();




        camera_view = (CameraView) findViewById(R.id.cameraView);
        camera_view.setLifecycleOwner(this);
        camera_view.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getVisionImageFromFrame(frame));



            }
        });

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        dectector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);


    }

    private void processImage(final FirebaseVisionImage image) {
        mShouldThrottle = new AtomicBoolean(false);
        if(!isDetected)
        {
            dectector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mShouldThrottle.set(false);


                                }
                            }, 1000);


                            processResult(firebaseVisionBarcodes);

                        }
                    })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mShouldThrottle.set(false);

                                    }
                                }, 1000);
                                Toast.makeText(qrScanner.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
               mShouldThrottle.set(true);
        }
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if (firebaseVisionBarcodes.size() == 1 || firebaseVisionBarcodes.size() == 0) {
            isDetected = true;
            btn_start_again.setEnabled(isDetected);

            for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {

                int value_type = item.getValueType();
                switch (value_type) {

                    case FirebaseVisionBarcode.TYPE_TEXT: {



                        createDialog(item.getRawValue()) ;
                        break;

                    }



             /*      case FirebaseVisionBarcode.TYPE_URL: {    // start browser intent
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRawValue()));
                        startActivity(intent);
                    }

                    break;

                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO:
                    {
                String info = new StringBuilder("Name: ")
                        .append(item.getContactInfo().getName().getFormattedName())
                        .append("\n")
                        .append("Address: ")
                        .append(item.getContactInfo().getAddresses().get(0).getAddressLines()[0])
                        .append("\n")
                        .append("Email: ")
                        .append(item.getContactInfo().getEmails().get(0).getAddress())
                        .toString();
                createDialog(info);
            break;

            }
             */
                        default:
                            Toast.makeText(this, "barcode couldn't scan", Toast.LENGTH_SHORT).show();
                            break;


                }
            }
        }
    }

    private void createDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(text)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();






    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        mMetadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize(). getHeight())
                .setWidth(frame.getSize().getWidth())

                .build();



        return FirebaseVisionImage.fromByteArray(data, mMetadata);

    }
}
