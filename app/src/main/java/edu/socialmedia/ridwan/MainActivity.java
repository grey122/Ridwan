package edu.socialmedia.ridwan;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.zxing.WriterException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import edu.socialmedia.ridwan.firebaseDb.FirebaseUtil;
import edu.socialmedia.ridwan.firebaseDb.Product;
import edu.socialmedia.ridwan.notification.ProductReminderBroadcastReceiver;


public class MainActivity extends AppCompatActivity {
    String TAG = "GenerateQrcode";
    EditText edtProductName, edtCompanyAddress, edtPhoneNumber, edtEmailAddress, edtWebsite;
    EditText edtmanuDate, edtExpdate;
    ImageView qrimg;
    Button start;
    Bitmap mBitmap;
    QRGEncoder mQRGEncoder;
    public String mProduct_name, companyAdress, mPhoneNumber, mEmailAddress, mWebsite, mManuDate, mExpDate;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Button generate_QRCode;
    ImageView qrCode;
    EditText mEditText;
    DatePickerDialog.OnDateSetListener mSetListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    public Product mProduct;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getProductString();


        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        saveImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "you must accept permission", Toast.LENGTH_SHORT).show();

                    }
                }).check();



       date();
        FirebaseUtil.openFbReference("product");

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;

        Intent intent = getIntent();
        Product product = (Product) intent.getSerializableExtra("product");
        if (product == null){
            product = new Product();
        }
        this.mProduct = product;
        edtProductName.setText(product.getP_name());
        edtCompanyAddress.setText(product.getP_company_name());
        edtmanuDate.setText(product.getP_manu_date());
        edtExpdate.setText(product.getP_exp_date());
        edtPhoneNumber.setText(product.getP_company_phone_number());
        edtWebsite.setText(product.getP_website());





    }

  public void qrCodeGenerator(View view) {
        qrimg = (ImageView) findViewById(R.id.qrcode);
        start = (Button) findViewById(R.id.createbtn);
         getProductString();


      if(mProduct_name.length()>0)

            {
                //SEARCH FOR WINDOW MANAGER
                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3 / 4;
                Bundle bundle = getBundle();
                mQRGEncoder = new QRGEncoder(null, bundle, QRGContents.Type.CONTACT, smallerDimension);

                try {
                    mBitmap = mQRGEncoder.encodeAsBitmap();
                    qrimg.setImageBitmap(mBitmap);
                    Toast.makeText(this, "barcode created", Toast.LENGTH_SHORT).show();

                } catch (WriterException e) {
                    Log.v(TAG, e.toString());
                }
                saveProduct();
                Toast.makeText(this, "data backed up to database", Toast.LENGTH_SHORT).show();

            }else

            {

                edtProductName.setError("Required");
            }

      }

    public void showReminderNotification(){

        String productName = mProduct.getP_name();

        Intent intent = new Intent(this, ProductReminderBroadcastReceiver.class);
        intent.putExtra(ProductReminderBroadcastReceiver.EXTRA_P_NAME, productName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long currentTimeInMillsecounds = SystemClock.elapsedRealtime();
        long ONE_HOUR = 60 * 60 * 1000;

        long alarmTime = currentTimeInMillsecounds + ONE_HOUR;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, alarmTime, pendingIntent);

    }



    public void clean(View view) {

        edtProductName.setText("");
        edtCompanyAddress.setText("");
        edtPhoneNumber.setText("");
        edtWebsite.setText("");
        edtmanuDate.setText("");
        edtExpdate.setText("");
        edtProductName.requestFocus();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       switch (item.getItemId()){
           case R.id.action_notification:
               showReminderNotification();
               return true;

           case R.id.action_delete_mennu:
               deleteProduct();
               Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
               backToList();
               return true;

               default:
                   return super.onOptionsItemSelected(item);

        }

    }

    private void saveProduct() {
        mProduct.setP_name(mProduct_name);
        mProduct.setP_company_name(companyAdress);
        mProduct.setP_manu_date(mManuDate);
        mProduct.setP_exp_date(mExpDate);
        mProduct.setP_company_phone_number(mPhoneNumber);
        mProduct.setP_website(mWebsite);


      if (mProduct.getId() == null){
          mDatabaseReference.push().setValue(mProduct);
      }
      else
      {
          mDatabaseReference.child(mProduct.getId()).setValue(mProduct);

      }


    }

    private void deleteProduct(){
        if (mProduct == null){
            Toast.makeText(this, "please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(mProduct.getId()).removeValue();
    }

    private void backToList(){
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);

    }

    private void getProductString() {
        //get id
        edtProductName = (EditText) findViewById(R.id.text_product_name);
        edtCompanyAddress = (EditText) findViewById(R.id.text_company_name);
        edtPhoneNumber = (EditText) findViewById(R.id.txt_company_number);
        edtWebsite =  (EditText) findViewById(R.id.edtWebsite);
        edtmanuDate = (EditText) findViewById(R.id.txt_date_manu);
        edtExpdate = (EditText) findViewById(R.id.txt_date_exp);

        //get String
        mProduct_name = edtProductName.getText().toString().trim();
        companyAdress = edtCompanyAddress.getText().toString().trim();
        mPhoneNumber = edtPhoneNumber.getText().toString();
        mWebsite = edtWebsite.getText().toString();
        mManuDate = edtmanuDate.getText().toString();
        mExpDate = edtExpdate.getText().toString();
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString( ContactsContract.Intents.Insert.NAME, mProduct_name);

        bundle.putString(ContactsContract.Intents.Insert.POSTAL, companyAdress);
        bundle.putString(QRGContents.URL_KEY, mWebsite);

        for (int x = 0; x < QRGContents.PHONE_KEYS.length; x++) {
            bundle.putString(QRGContents.PHONE_KEYS[x], mPhoneNumber);
        }

        for (int x = 0; x < QRGContents.EMAIL_KEYS.length; x++) {
            bundle.putString(QRGContents.EMAIL_KEYS[x], mExpDate);
        }



        return bundle;
    }

    public void saveImage(){
        Button saveBtn = (Button) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean save;
                String result;

                try {
                    save = QRGSaver.save(savePath, edtProductName.getText().toString().trim(), mBitmap, QRGContents.ImageType.IMAGE_JPEG);
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

        edtExpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month +1;
                        String date = day+"/"+month+"/"+year;
                        edtExpdate.setText(date);


                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

    }





}

