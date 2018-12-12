package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.UserInfo;


public class RegisterActivity extends AppCompatActivity {

    private EditText nameET;
    private EditText usernameET;
    private EditText emailET;
    private EditText passwordET;
    private Button createAccountBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Set up the registration form.
        nameET = (EditText) findViewById(R.id.nameET);
        usernameET = (EditText) findViewById(R.id.usernameET);
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        createAccountBtn = (Button) findViewById(R.id.createAccBtn);


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(emailET.getText().toString(), passwordET.getText().toString());
            }
        });

    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Account successfully created
                            Toast.makeText(getApplicationContext(), "Account Created...", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                            mDatabase.child("name").setValue(nameET.getText().toString());
                            mDatabase.child("username").setValue(usernameET.getText().toString());
                            mDatabase.child("email").setValue(emailET.getText().toString());

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("FIRSTNAME", nameET.getText().toString());
                            intent.putExtra("USERNAME", usernameET.getText().toString());
                            intent.putExtra("EMAIL", emailET.getText().toString());

                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("Account creation failed...");
//                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
//                            System.out.println(e);

                        }

                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailET.setError("Required.");
            valid = false;
        } else {
            emailET.setError(null);
        }

        String password = passwordET.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordET.setError("Required.");
            valid = false;
        } else if(password.length() < 6) {
            passwordET.setError("Length needs to be at least six characters.");
        } else {
            passwordET.setError(null);
        }

        String name = nameET.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameET.setError("Required.");
            valid = false;
        } else {
            nameET.setError(null);
        }

        String username = usernameET.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameET.setError("Required.");
            valid = false;
        } else {
            usernameET.setError(null);
        }

        return valid;
    }
}
