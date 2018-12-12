package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class SplitReceiptFragment extends DialogFragment {
    View view;
    Context context;
    Button doneBtn;
    boolean missingPayer;

    RecyclerView recyclerView;
    ReceiptItemAdapter receiptItemAdapter;
    SplitPricesFragment splitPricesFragment;

    ArrayList<String> productsList;
    ArrayList<String> pricesList;
    ArrayList<String> usersList;
    ArrayList<String> usersEmailList;

    String mainUsername;

    double[] userPayments = new double[4];

    public SplitReceiptFragment() {}

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

        productsList = getArguments().getStringArrayList("products");
        pricesList = getArguments().getStringArrayList("prices");
        usersList = getArguments().getStringArrayList("users");
        usersEmailList = getArguments().getStringArrayList("useremails");
        mainUsername = getArguments().getString("mainUsername");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.split_receipt_activity, container, false);
        doneBtn = (Button) view.findViewById(R.id.doneBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);


        productsList.remove("SUBTOTAL");

        //set adapter to display all products, prices, and checkboxes in recycler view
        receiptItemAdapter = new ReceiptItemAdapter(productsList, pricesList, usersList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(receiptItemAdapter);

        //determine which checkboxes clicked
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] checkedStatesA = receiptItemAdapter.getmCheckedStateA();
                boolean[] checkedStatesB = receiptItemAdapter.getmCheckedStateB();
                boolean[] checkedStatesC = receiptItemAdapter.getmCheckedStateC();
                boolean[] checkedStatesD = receiptItemAdapter.getmCheckedStateD();
                //split each product price based on checkboxes clicked
                calculateDebt(checkedStatesA, checkedStatesB, checkedStatesC, checkedStatesD);
            }
        });

        return view;
    }

    private void calculateDebt(boolean[] checkedStatesA, boolean[] checkedStatesB, boolean[] checkedStatesC, boolean[] checkedStatesD) {
        int divider;
        //saves user debts
        userPayments = new double[4];

        //price based on how many users split
        double[] newItemPrices = new double[receiptItemAdapter.getItemCount()];

        //true if product has no payer
        missingPayer = false;

        //determine number of payers per product
        for(int i = 0; i < receiptItemAdapter.getItemCount(); i++) {
            divider = 0;

            if(checkedStatesA[i]) {
                divider++;
            }
            if(checkedStatesB[i]) {
                divider++;
            }
            if(checkedStatesC[i]) {
                divider++;
            }
            if(checkedStatesD[i]) {
                divider++;
            }

            //check if all items paid for
            if(divider == 0){
                missingPayer = true;
                Toast.makeText(getActivity(), "Missing payer for " + productsList.get(i),
                        Toast.LENGTH_LONG).show();
            }
            else{
                //divide price
                newItemPrices[i] = Double.parseDouble(pricesList.get(i).substring(1))/divider;
            }

        }

        //add up debts per user
        for(int i = 0; i < newItemPrices.length; i++) {
            if(checkedStatesA[i]) {
                userPayments[0] += newItemPrices[i];
            }
            if(checkedStatesB[i]) {
                userPayments[1] += newItemPrices[i];
            }
            if(checkedStatesC[i]) {
                userPayments[2] += newItemPrices[i];
            }
            if(checkedStatesD[i]) {
                userPayments[3] += newItemPrices[i];
            }
        }

        for(double p : userPayments)
            Log.e("userpayments", p + "");

        //show next fragment if all items paid for
        if(!missingPayer)
             displayFragment();
    }

    public void displayFragment(){
        splitPricesFragment = new SplitPricesFragment();
        splitPricesFragment.setCancelable(false);

        Bundle args = new Bundle();
        args.putDoubleArray("debtList", userPayments);
        args.putStringArrayList("users", usersList);
        args.putStringArrayList("useremails", usersEmailList);
        args.putString("mainUsername", mainUsername);
        splitPricesFragment.setArguments(args);
        splitPricesFragment.show(getFragmentManager(), "show split prices fragment");
    }

}
