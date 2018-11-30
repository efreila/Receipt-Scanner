package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class SplitReceiptFragment extends DialogFragment {
    View view;
    GridView gridView;
    Context context;
    ItemAdapter adapter;
    Button doneBtn;

    ArrayList<String> productsList;
    ArrayList<String> pricesList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.split_receipt_activity, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        doneBtn = (Button) view.findViewById(R.id.doneBtn);

        adapter = new ItemAdapter(getActivity(), productsList, pricesList);
        gridView.setAdapter(adapter);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGridView(gridView);
            }
        });

        return view;
    }

    private void checkGridView(GridView gridView) {
        boolean[] checks;
        int counter;
        View v;

        CheckBox firstCheckBox;
        CheckBox secondCheckBox;
        CheckBox thirdCheckBox;
        CheckBox fourthCheckBox;

        for (int i = 0; i < gridView.getChildCount() ; i++ ){
            v = gridView.getChildAt(i);
            firstCheckBox = (CheckBox) v.findViewById(R.id.checkBox0);
            secondCheckBox = (CheckBox) v.findViewById(R.id.checkBox1);
            thirdCheckBox = (CheckBox) v.findViewById(R.id.checkBox2);
            fourthCheckBox = (CheckBox) v.findViewById(R.id.checkBox3);

            checks = new boolean[4];
            counter = 0;

            if(firstCheckBox.isChecked()){
                counter++;
                checks[0] = true;
            }
            if(secondCheckBox.isChecked()){
                counter++;
                checks[1] = true;
            }
            if(thirdCheckBox.isChecked()){
                counter++;
                checks[2] = true;
            }
            if(fourthCheckBox.isChecked()){
                counter++;
                checks[3] = true;
            }

            double dividedPrice = Double.parseDouble(pricesList.get(i).substring(1))/counter;
            Log.e("price divided", dividedPrice + "");
        }
    }

}
