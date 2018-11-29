package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
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
    private Button takePhotoBtn;
    private Button galleryBtn;
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
    private String itemNumber = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        galleryBtn = findViewById(R.id.galleryBtn);

        FirebaseApp.initializeApp(this);

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> products = new ArrayList<String>();
                ArrayList<String> prices = new ArrayList<String>();
                for (Product p : productsList){
                    products.add(p.getName());
                    prices.add("$" + String.format("%.2f", p.getPrice()));
                }
                splitReceiptFragment = new SplitReceiptFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("products", products);
                args.putStringArrayList("prices", prices);
                splitReceiptFragment.setArguments(args);
                splitReceiptFragment.show(getSupportFragmentManager(), "show split receipt fragment");
            }
        });


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
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
                detectText();
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
                                        for (FirebaseVisionDocumentText.Word word: paragraph.getWords()) {
                                            String wordText = word.getText();
//                                            System.out.println(wordText);
                                            if(wordText.equals("CPN")) {
                                                isCoupon = true;
                                            } else if(wordText.equals("REDEMP")) {
                                                isTax = true;
                                                continue;
                                            } else if(wordText.equals("SUBTOTAL")) {
                                                //END FU
                                                // NCTION
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

                                                    productsList.add(new Product(currItem, Double.parseDouble(currItemPrice)));

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

                            for (Product p : productsList)
                                Log.e("Product", p.name + " price " + p.price);
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

}
