package edu.socialmedia.ridwan;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;


public class MainActivity extends AppCompatActivity {
    String TAG = "GenerateQrcode";
    EditText edttxt, edtCompanyAddress, edtPhoneNumber, edtEmailAddress, edtWebsite;
    EditText edtmanuDate, edtExpdate;
    ImageView qrimg;
    Button start;
    Bitmap mBitmap;
    QRGEncoder mQRGEncoder;
    public String mInputValue, companyAdress, mPhoneNumber, mEmailAddress, mWebsite, mManuDate, mExpDate;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Button generate_QRCode;
    ImageView qrCode;
    EditText mEditText;
    DatePickerDialog.OnDateSetListener mSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date();
        save();
        qrCodeGenerator();

  /*      Dexter.withActivity(this)
               .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        save();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "you must accept permission", Toast.LENGTH_SHORT).show();

                    }
                }).check();*/
    }

  public void qrCodeGenerator() {
        qrimg = (ImageView) findViewById(R.id.qrcode);

        start = (Button) findViewById(R.id.createbtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactString();

                if (mInputValue.length() >0){
                    //SEARCH FOR WINDOW MANAGER
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width:height;
                    smallerDimension = smallerDimension * 3/4;
                    Bundle bundle = getBundle();
                    mQRGEncoder = new QRGEncoder(null, bundle, QRGContents.Type.CONTACT, smallerDimension);

                    try {
                        mBitmap=mQRGEncoder.encodeAsBitmap();
                        qrimg.setImageBitmap(mBitmap);

                    }
                    catch ( WriterException e){
                        Log.v(TAG, e.toString());
                    }


                }else {

                    edttxt.setError("Required");
                }



            }
        });
    }

    private void getContactString() {
        //get id
        edttxt = (EditText) findViewById(R.id.text_product_name);
        edtCompanyAddress = (EditText) findViewById(R.id.text_company_name);
        edtPhoneNumber = (EditText) findViewById(R.id.txt_company_number);
        edtEmailAddress = (EditText) findViewById(R.id.text_com_emai_add);
        edtWebsite = (EditText) findViewById(R.id.tex_com_web_link);
        edtmanuDate = (EditText) findViewById(R.id.txt_date_manu);

        //convert to string
        mInputValue = edttxt.getText().toString().trim();
        companyAdress = edtCompanyAddress.getText().toString().trim();
        mPhoneNumber = edtPhoneNumber.getText().toString();
        mEmailAddress = edtEmailAddress.getText().toString();
        mWebsite = edtWebsite.getText().toString();
        mManuDate = edtmanuDate.getText().toString();
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(ContactsContract.Intents.Insert.NAME, mInputValue );
        bundle.putString(ContactsContract.Intents.Insert.NAME, mManuDate );
        bundle.putString(ContactsContract.Intents.Insert.POSTAL, companyAdress);
        bundle.putString(QRGContents.URL_KEY, mWebsite);

        for (int x = 0; x < QRGContents.PHONE_KEYS.length; x++) {
            bundle.putString(QRGContents.PHONE_KEYS[x], mPhoneNumber);
        }

        for (int x = 0; x < QRGContents.EMAIL_KEYS.length; x++) {
            bundle.putString(QRGContents.EMAIL_KEYS[x], mEmailAddress);
        }



        return bundle;
    }


    public void save (){

        Button saveBtn = (Button) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean save;
                String result;
                try {
                    save = QRGSaver.save(savePath, edttxt.getText().toString().trim(), mBitmap, QRGContents.ImageType.IMAGE_JPEG);
                    result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    public void date(){
        edtmanuDate = findViewById(R.id.txt_date_manu);
        edtExpdate = findViewById(R.id.txt_date_exp);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        edtmanuDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month +1;
                        String date = day+"/"+month+"/"+year;
                        edtmanuDate.setText(date);


                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

    }





}

