package id.kopas.berkarya.said.ibanking.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import id.kopas.berkarya.said.ibanking.R;
import id.kopas.berkarya.said.ibanking.models.Branch;

public class AdapterBranch extends ArrayAdapter<Branch> {
    private final Context context;
    private final ArrayList<Branch> lists;

    public AdapterBranch(@NonNull Context context, int resource, @NonNull ArrayList<Branch> objects) {
        super(context, resource, objects);
        lists = objects;
        this.context = context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_branch, parent, false);

        Branch item = lists.get(position);

        TextView tv1 = rowView.findViewById(R.id.tv1);
        tv1.setText(item.nama);

        return rowView;
    }
}

