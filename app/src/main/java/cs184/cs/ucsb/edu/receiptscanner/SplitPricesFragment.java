package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class SplitPricesFragment extends DialogFragment {
    View view;
    Context context;
    Button finishBtn;

    TextView firstUserDebt;
    TextView secondUserDebt;
    TextView thirdUserDebt;
    TextView fourthUserDebt;

    double[] debtList;

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

        debtList = getArguments().getDoubleArray("debtList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.split_prices_activity, container, false);

        firstUserDebt = (TextView) view.findViewById(R.id.firstUserDebt);
        secondUserDebt = (TextView) view.findViewById(R.id.secondUserDebt);
        thirdUserDebt = (TextView) view.findViewById(R.id.thirdUserDebt);
        fourthUserDebt = (TextView) view.findViewById(R.id.fourthUserDebt);

        firstUserDebt.setText("$" + String.format("%.2f", debtList[0]));
        secondUserDebt.setText("$" + String.format("%.2f", debtList[1]));
        thirdUserDebt.setText("$" + String.format("%.2f", debtList[2]));
        fourthUserDebt.setText("$" + String.format("%.2f", debtList[3]));

        finishBtn = (Button) view.findViewById(R.id.finishBtn);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        return view;
    }
}
