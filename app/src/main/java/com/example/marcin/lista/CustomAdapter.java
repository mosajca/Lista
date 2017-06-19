package com.example.marcin.lista;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Row> {
    private Context main;

    public CustomAdapter(List<Row> array, Context context, Context main) {
        super(context, R.layout.row, array);
        this.main = main;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
        }

        final Row row = getItem(position);
        final TextView amount = (TextView) view.findViewById(R.id.amount);
        TextView name = (TextView) view.findViewById(R.id.name);
        Button plus = (Button) view.findViewById(R.id.plus);
        Button minus = (Button) view.findViewById(R.id.minus);

        if (row != null && amount != null && name != null && plus != null && minus != null) {
            name.setText(row.getName());
            amount.setText(String.valueOf(row.getAmount()));
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int value = row.getAmount();
                    int newValue = (value == Integer.MAX_VALUE) ? value : value + 1;
                    amount.setText(String.valueOf(newValue));
                    row.setAmount(newValue);
                    ((MainActivity) main).updateAmount(row);
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int value = row.getAmount();
                    int newValue = (value == Integer.MIN_VALUE) ? value : value - 1;
                    amount.setText(String.valueOf(newValue));
                    row.setAmount(newValue);
                    ((MainActivity) main).updateAmount(row);
                }
            });
        }
        return view;
    }
}