package cs184.cs.ucsb.edu.receiptscanner;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.MyViewHolder> {
    private ArrayList<String> productsList;
    private ArrayList<String> pricesList;
    private ArrayList<String> usersList;
    private final boolean[] mCheckedStateA;
    private final boolean[] mCheckedStateB;
    private final boolean[] mCheckedStateC;
    private final boolean[] mCheckedStateD;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product, price;
        public CheckBox checkBox0, checkBox1, checkBox2, checkBox3;

        public MyViewHolder(View view) {
            super(view);
            product = (TextView) view.findViewById(R.id.productTextView);
            price = (TextView) view.findViewById(R.id.priceTextView);

            checkBox0 = (CheckBox) view.findViewById(R.id.checkBox0);
            checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
            checkBox2 = (CheckBox) view.findViewById(R.id.checkBox2);
            checkBox3 = (CheckBox) view.findViewById(R.id.checkBox3);
        }
    }

    public ReceiptItemAdapter(ArrayList<String> productsList, ArrayList<String> pricesList, ArrayList<String> usersList) {
        this.productsList = productsList;
        this.pricesList = pricesList;
        this.usersList = usersList;

        //saves state of checkbox for each user
        mCheckedStateA = new boolean[productsList.size()];
        mCheckedStateB = new boolean[productsList.size()];
        mCheckedStateC = new boolean[productsList.size()];
        mCheckedStateD = new boolean[productsList.size()];
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String currProduct = productsList.get(position);
        String currPrice = pricesList.get(position);

        holder.product.setText(currProduct);
        holder.price.setText(currPrice);

        //determine number of users to display (2 users)
        holder.checkBox0.setText(usersList.get(0));
        holder.checkBox1.setText(usersList.get(1));
        holder.checkBox2.setVisibility(View.INVISIBLE);
        holder.checkBox3.setVisibility(View.INVISIBLE);

        //if we have 3 users
        if(usersList.size() == 3) {
            holder.checkBox2.setText(usersList.get(2));
            holder.checkBox2.setVisibility(View.VISIBLE);
        }

        //four users
        if(usersList.size() == 4) {
            holder.checkBox2.setText(usersList.get(2));
            holder.checkBox2.setVisibility(View.VISIBLE);
            holder.checkBox3.setText(usersList.get(3));
            holder.checkBox3.setVisibility(View.VISIBLE);
        }

        //listener for checkboxes corresponding to first user
        holder.checkBox0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckedStateA[position] = true;
                } else {
                    mCheckedStateA[position] = false;
                }
                Log.e("Position ", position + " product: " + productsList.get(position) + "checkedA " +mCheckedStateA[position]);
            }
        });

        //listener for checkboxes corresponding to second user
        holder.checkBox1.setOnCheckedChangeListener(new   CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckedStateB[position] = true;
                } else {
                    mCheckedStateB[position] = false;
                }
                Log.e("Position ", position + " product: " + productsList.get(position) + "checkedB " + mCheckedStateB[position]);
            }

        });

        //listener for checkboxes corresponding to third user
        holder.checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckedStateC[position] = true;
                } else {
                    mCheckedStateC[position] = false;
                }
                Log.e("Position ", position + " product: " + productsList.get(position) + "checkedC " + mCheckedStateC[position]);

            }
        });

        //listener for checkboxes corresponding to fourth user
        holder.checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckedStateD[position] = true;
                } else {
                    mCheckedStateD[position] = false;
                }
                Log.e("Position ", position + " product: " + productsList.get(position) + "checkedD " + mCheckedStateD[position]);

            }
        });

        //saving checkbox state when scrolling
        if(mCheckedStateA[position]) {
            holder.checkBox0.setChecked(true);
        } else {
            holder.checkBox0.setChecked(false);
        }

        if(mCheckedStateB[position]) {
            holder.checkBox1.setChecked(true);
        } else {
            holder.checkBox1.setChecked(false);
        }

        if(mCheckedStateC[position]) {
            holder.checkBox2.setChecked(true);
        } else {
            holder.checkBox2.setChecked(false);
        }

        if(mCheckedStateD[position]) {
            holder.checkBox3.setChecked(true);
        } else {
            holder.checkBox3.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public boolean[] getmCheckedStateA() {
        return mCheckedStateA;
    }

    public boolean[] getmCheckedStateB() {
        return mCheckedStateB;
    }

    public boolean[] getmCheckedStateC() {
        return mCheckedStateC;
    }

    public boolean[] getmCheckedStateD() {
        return mCheckedStateD;
    }

}