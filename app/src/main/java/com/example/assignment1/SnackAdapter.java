package com.example.assignment1;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
public class SnackAdapter extends BaseAdapter {
    private Context context;
    private List<Snack> snackList;
    private int[] quantities;
    private OnQuantityChangedListener listener;
    public interface OnQuantityChangedListener {
        void onQuantityChanged();
    }
    public SnackAdapter(Context context, List<Snack> snackList) {
        this.context = context;
        this.snackList = snackList;
        this.quantities = new int[snackList.size()];
    }
    public void setOnQuantityChangedListener(OnQuantityChangedListener listener) {
        this.listener = listener;
    }
    @Override
    public int getCount() {
        return snackList.size();
    }
    @Override
    public Object getItem(int position) {
        return snackList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public int getQuantity(int position) {
        return quantities[position];
    }
    public double getTotalPrice() {
        double total = 0;
        for (int i = 0; i < snackList.size(); i++) {
            total += snackList.get(i).getPrice() * quantities[i];
        }
        return total;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_snack, parent, false);
            holder = new ViewHolder();
            holder.imgSnack = convertView.findViewById(R.id.imgSnack);
            holder.txtSnackName = convertView.findViewById(R.id.txtSnackName);
            holder.txtSnackDesc = convertView.findViewById(R.id.txtSnackDesc);
            holder.txtSnackPrice = convertView.findViewById(R.id.txtSnackPrice);
            holder.btnMinus = convertView.findViewById(R.id.btnMinus);
            holder.txtQty = convertView.findViewById(R.id.txtQty);
            holder.btnPlus = convertView.findViewById(R.id.btnPlus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Snack snack = snackList.get(position);
        holder.imgSnack.setImageResource(snack.getImageResId());
        holder.txtSnackName.setText(snack.getName());
        holder.txtSnackDesc.setText(snack.getDescription());
        holder.txtSnackPrice.setText(String.format("$%.2f", snack.getPrice()));
        holder.txtQty.setText(String.valueOf(quantities[position]));
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantities[position]++;
                notifyDataSetChanged();
                if (listener != null) listener.onQuantityChanged();
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantities[position] > 0) {
                    quantities[position]--;
                    notifyDataSetChanged();
                    if (listener != null) listener.onQuantityChanged();
                }
            }
        });
        return convertView;
    }
    private static class ViewHolder {
        ImageView imgSnack;
        TextView txtSnackName;
        TextView txtSnackDesc;
        TextView txtSnackPrice;
        Button btnMinus;
        TextView txtQty;
        Button btnPlus;
    }
}
