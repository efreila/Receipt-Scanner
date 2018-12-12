package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SplitPricesFragment extends DialogFragment {
    View view;
    Context context;
    Button finishBtn;
    Button sendEmailBtn;

    TextView firstUserDebt, secondUserDebt, thirdUserDebt, fourthUserDebt;
    TextView firstUsername, secondUsername, thirdUsername, fourthUsername;

    double[] debtList;
    ArrayList<String> users;
    ArrayList<String> emails;
    String mainUser;

    public SplitPricesFragment() {}

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point dimensions = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(dimensions);

        window.setLayout((int)(dimensions.x*.8), (int)(dimensions.y*.8));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        users = getArguments().getStringArrayList("users");
        debtList = getArguments().getDoubleArray("debtList");
        emails = getArguments().getStringArrayList("useremails");
        mainUser = getArguments().getString("mainUsername");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.split_prices_activity, container, false);

        //display usernames
        firstUsername = (TextView) view.findViewById(R.id.firstUserName);
        secondUsername = (TextView) view.findViewById(R.id.secondUserName);
        thirdUsername = (TextView) view.findViewById(R.id.thirdUserName);
        fourthUsername = (TextView) view.findViewById(R.id.fourthUserName);

        //display user debts
        firstUserDebt = (TextView) view.findViewById(R.id.firstUserDebt);
        secondUserDebt = (TextView) view.findViewById(R.id.secondUserDebt);
        thirdUserDebt = (TextView) view.findViewById(R.id.thirdUserDebt);
        fourthUserDebt = (TextView) view.findViewById(R.id.fourthUserDebt);


        firstUsername.setText(users.get(0));
        secondUsername.setText(users.get(1));
        if(users.size() == 3){
            thirdUsername.setText(users.get(2));
            fourthUsername.setVisibility(View.INVISIBLE);
            fourthUserDebt.setVisibility(View.INVISIBLE);
        }
        else if(users.size() == 4){
            thirdUsername.setText(users.get(2));
            fourthUsername.setText(users.get(3));
        }
        else{
            thirdUsername.setVisibility(View.INVISIBLE);
            thirdUserDebt.setVisibility(View.INVISIBLE);
            fourthUsername.setVisibility(View.INVISIBLE);
            fourthUserDebt.setVisibility(View.INVISIBLE);
        }

        //display with appropriate precision
        firstUserDebt.setText("$" + String.format("%.2f", debtList[0]));
        secondUserDebt.setText("$" + String.format("%.2f", debtList[1]));
        thirdUserDebt.setText("$" + String.format("%.2f", debtList[2]));
        fourthUserDebt.setText("$" + String.format("%.2f", debtList[3]));


        finishBtn = (Button) view.findViewById(R.id.finishBtn);
        sendEmailBtn = (Button) view.findViewById(R.id.sendEmailBtn);


        //restart main activity
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int k = 1; k < users.size(); k++) {
                    System.out.println(emails.get(k));
                    sendEmail(users.get(0), String.format("%.2f", debtList[k]), emails.get(k));
                }

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("FIRSTNAME", users.get(0));
                intent.putExtra("USERNAME", mainUser);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }


    public void sendEmail(String person, String amount, String email) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "Your recent shopping trip...");
        i.putExtra(Intent.EXTRA_TEXT, "You owe " + person + " " + amount + " dollars for your last shopping trip.");

        try {
            startActivityForResult(
                    Intent.createChooser(i, "Send shopping charge..."),
                    1500
            );

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(
                    getContext(),
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

}
