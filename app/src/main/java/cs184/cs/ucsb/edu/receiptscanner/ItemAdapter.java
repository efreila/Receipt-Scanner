package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> productsList;
    private ArrayList<String> pricesList;
    private LayoutInflater inflater;

    public ItemAdapter(Context c, ArrayList<String> productsList, ArrayList<String> pricesList) {
        this.context = c;
        this.productsList = productsList;
        this.pricesList = pricesList;
    }

    public int getCount() {
        return productsList.size();
    }

    public Object getItem(int position){
        return new Product(productsList.get(position), Double.parseDouble(pricesList.get(position)));
    }

    public Object getProduct(int position) {
        return productsList.get(position);
    }

    public Object getPrice(int position) {
        return pricesList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.receipt_item, null);
        }
        TextView product = (TextView) gridView.findViewById(R.id.productTextView);
        TextView price = (TextView) gridView.findViewById(R.id.priceTextView);

        CheckBox checkBox = (CheckBox) gridView.findViewById(R.id.checkBox0);
        CheckBox checkBoxOne = (CheckBox) gridView.findViewById(R.id.checkBox0);
        CheckBox checkBoxTwo = (CheckBox) gridView.findViewById(R.id.checkBox0);
        CheckBox checkBoxThree = (CheckBox) gridView.findViewById(R.id.checkBox0);



        product.setText(productsList.get(position));
        price.setText(pricesList.get(position));

        return gridView;
    }

}
