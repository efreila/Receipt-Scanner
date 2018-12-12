package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private SplitReceiptFragment splitReceiptFragment;
    private Button galleryBtn;
    private TextView loadingTextView;
    private TextView userNameHint;
    private TextView welcomeTV;
    private EditText userName1;
    private EditText userName2;
    private EditText userName3;
    private EditText userName4;
    private Bitmap bitmap;
    private boolean detectedFirstItem = false;
    private boolean addToItem = false;
    private boolean addToPrice = false;
    private boolean newItem = true;
    private boolean isCoupon = false;
    private boolean isTax = false;
    private String currItem = "";
    private String currItemPrice = "";
    ArrayList<Product> productsList = new ArrayList<Product>();
    ArrayList<String> users = new ArrayList<String>();
    ArrayList<String> userEmails = new ArrayList<String>();


    boolean taskDone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        galleryBtn = findViewById(R.id.galleryBtn);
        loadingTextView = findViewById(R.id.loadingTextView);
        loadingTextView.setVisibility(View.INVISIBLE);
        userNameHint = findViewById(R.id.userNameHint);
        userName1 = findViewById(R.id.userName1);
        userName2 = findViewById(R.id.userName2);
        userName3 = findViewById(R.id.userName3);
        userName4 = findViewById(R.id.userName4);
        welcomeTV = findViewById(R.id.welcomeTV);
        welcomeTV.setText("Welcome " + intent.getStringExtra("FIRSTNAME"));
        userName1.setText(intent.getStringExtra("USERNAME"));
        userName1.setFocusable(false);

        users.add(intent.getStringExtra("FIRSTNAME"));
        userEmails.add(intent.getStringExtra("EMAIL"));


        FirebaseApp.initializeApp(this);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userCounter = 1;
                if (!userName2.getText().toString().equals(""))
                    userCounter++;
                if (!userName3.getText().toString().equals(""))
                    userCounter++;
                if (!userName4.getText().toString().equals(""))
                    userCounter++;

                if (userCounter == 1) {
                    Toast.makeText(getApplicationContext(), "You must have at least 2 users.", Toast.LENGTH_SHORT).show();

                }
                else {
                 /* Check for valid usernames in the Firebase DB */
                    final int uCount = userCounter;
                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        int usersVerified = 1;
                        boolean showToast = true;
                        @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String uUserName = snapshot.child("username").getValue(String.class);

                                        if(uUserName.equals(userName2.getText().toString()) || uUserName.equals(userName3.getText().toString()) || uUserName.equals(userName4.getText().toString())) {
                                            users.add(snapshot.child("name").getValue(String.class));
                                            userEmails.add(snapshot.child("email").getValue(String.class));
                                            usersVerified++;
                                        }

                                        if(usersVerified == uCount) {
                                            showToast = false;
                                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                            startActivityForResult(intent, 1);
                                        }
                                    }

                                    if(showToast)
                                        Toast.makeText(getApplicationContext(), "Please enter valid usernames...", Toast.LENGTH_SHORT).show();



                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });



//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                new MyAsyncTask().execute();
                //detectText();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void detectText() {
        if(bitmap == null) {
            Toast.makeText(this, "Bitmap is NULL", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionCloudDocumentRecognizerOptions options = new FirebaseVisionCloudDocumentRecognizerOptions.Builder().setLanguageHints(Arrays.asList("en")).build();
            FirebaseVisionDocumentTextRecognizer textRecognizer = FirebaseVision.getInstance().getCloudDocumentTextRecognizer(options);

            textRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                        @Override
                        public void onSuccess(FirebaseVisionDocumentText result) {
                            for (FirebaseVisionDocumentText.Block block: result.getBlocks()) {
                                for (FirebaseVisionDocumentText.Paragraph paragraph: block.getParagraphs()) {
                                    String paragraphText = paragraph.getText();

                                    if(detectedFirstItem == false) {
                                        for(int i = 0; i < paragraphText.length(); i++) {
                                            if(paragraphText.charAt(i) == '.') {
                                                detectedFirstItem = true;
                                                break;
                                            }
                                        }
                                    }

                                    if(detectedFirstItem) {
                                        //determine type of item
                                        for (FirebaseVisionDocumentText.Word word: paragraph.getWords()) {
                                            String wordText = word.getText();
//                                            System.out.println(wordText);
                                            if(wordText.equals("CPN")) {
                                                isCoupon = true;
                                            } else if(wordText.equals("REDEMP")) {
                                                isTax = true;
                                                continue;
                                            } else if(wordText.equals("SUBTOTAL")) {
                                                //END FUNCTION
                                            }

                                            if(TextUtils.isDigitsOnly(wordText) && wordText.length() <= 7) {
                                                if(addToItem) {
                                                    addToItem = false;
                                                    addToPrice = true;
                                                }

                                                else if(addToPrice) {
                                                    currItemPrice += wordText;
                                                    if(isCoupon) {
                                                        currItem = "CPN";
                                                        isCoupon = false;
                                                    } else if(isTax) {
                                                        currItem = "TAX";
                                                        isTax = false;
                                                    } else if(currItem == "") {
                                                        currItem = "CPN";
                                                    }

                                                    if(currItem == "CPN"){
                                                        //subtract from previously parsed product price
                                                        productsList.get(productsList.size() - 1).price -= Double.parseDouble(currItemPrice);
                                                    } else if (currItem == "TAX"){
                                                        //add to previously parsed product price
                                                        productsList.get(productsList.size() - 1).price += Double.parseDouble(currItemPrice);
                                                    }else{
                                                        //normal item
                                                        productsList.add(new Product(currItem, Double.parseDouble(currItemPrice)));
                                                    }

                                                    //reset variables
                                                    currItem = "";
                                                    currItemPrice = "";
                                                    addToPrice = false;
                                                    continue;
                                                } else if (newItem) {
                                                    if(isTax) {
                                                        addToPrice = true;
                                                    } else {
                                                        addToItem = true;
                                                        continue;
                                                    }

                                                }

                                            }

                                            if(addToItem) {
                                                if(currItem == "" && wordText.matches("[A-Za-z0-9]+")) {
                                                    currItem += wordText;

                                                } else {
                                                    if(wordText.matches("[A-Za-z0-9]+")) {
                                                        currItem += " ";
                                                        currItem += wordText;
                                                    }
                                                }
                                            }

                                            else if(addToPrice) {
                                                if(currItemPrice == "") {
                                                    currItemPrice += wordText;
                                                    currItemPrice += ".";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            taskDone = true;

                            for (Product p : productsList)
                                Log.d("Product", p.name + " price " + p.price);
                                //System.out.println("Product: " + p.name + ", Price: " + p.price);
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                }
                            });

        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //while scanning receipt
            galleryBtn.setVisibility(View.INVISIBLE);
            userNameHint.setVisibility(View.INVISIBLE);
            userName1.setVisibility(View.INVISIBLE);
            userName2.setVisibility(View.INVISIBLE);
            userName3.setVisibility(View.INVISIBLE);
            userName4.setVisibility(View.INVISIBLE);
            loadingTextView.setVisibility(View.VISIBLE);
            welcomeTV.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //scan receipt in background
            detectText();
            while(taskDone == false){
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ArrayList<String> products = new ArrayList<String>();
            ArrayList<String> prices = new ArrayList<String>();
            for (Product p : productsList){
                products.add(p.getName());
                prices.add("$" + String.format("%.2f", p.getPrice()));
            }

//            ArrayList<String> users = new ArrayList<String>();
//            if(!userName1.getText().toString().equals("")) {
//                users.add(userName1.getText().toString());
//            }
//            if(!userName2.getText().toString().equals("")) {
//                users.add(userName2.getText().toString());
//            }
//            if(!userName3.getText().toString().equals("")) {
//                users.add(userName3.getText().toString());
//            }
//            if(!userName4.getText().toString().equals("")) {
//                users.add(userName4.getText().toString());
//            }

            //start split receipt fragment
            splitReceiptFragment = new SplitReceiptFragment();
            splitReceiptFragment.setCancelable(false);
            Bundle args = new Bundle();
            args.putStringArrayList("products", products);
            args.putStringArrayList("prices", prices);
            args.putStringArrayList("users", users);
            args.putString("mainUsername", userName1.getText().toString());
            args.putStringArrayList("useremails", userEmails);
            splitReceiptFragment.setArguments(args);
            splitReceiptFragment.show(getSupportFragmentManager(), "show split receipt fragment");
        }
    }

}
