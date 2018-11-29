package cs184.cs.ucsb.edu.receiptscanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

    public Object getItem(int position) {
        return productsList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        String currentURI;
        TextView textView;

        if (convertView == null) {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.receipt_item, null);
        }
        TextView product = (TextView)gridView.findViewById(R.id.productTextView);
        TextView price = (TextView)gridView.findViewById(R.id.priceTextView);
        product.setText(productsList.get(position));
        price.setText(pricesList.get(position));

        return gridView;
    }
}
