package org.c_base.yeahletsdothat;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.List;
import org.c_base.yeahletsdothat.model.Perk;

class PerkAdapter extends BaseAdapter implements SpinnerAdapter {

    private Activity context;
    private final List<Perk> perks;

    PerkAdapter(final Activity context, final List<Perk> perks) {
        this.context = context;
        this.perks = perks;
    }

    @Override
    public int getCount() {
        return perks.size();
    }

    @Override
    public Perk getItem(final int position) {
        return perks.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final LayoutInflater from = LayoutInflater.from(parent.getContext());

        final View inflate = from.inflate(R.layout.item_perk, parent, false);

        final TextView title = (TextView) inflate.findViewById(R.id.title);

        title.setText(getItem(position).title + "("+formatAmount(getItem(position).amount)+")");

        return inflate;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {

        final LayoutInflater from = LayoutInflater.from(parent.getContext());

        final View inflate = from.inflate(R.layout.item_perk_dropdown, parent, false);

        final TextView title = (TextView) inflate.findViewById(R.id.title);
        final TextView price = (TextView) inflate.findViewById(R.id.price);
        final TextView text = (TextView) inflate.findViewById(R.id.text);

        final Perk item = getItem(position);
        title.setText(item.title);
        price.setText(formatAmount(item.amount) + " / " + item.available + " verfügbar");
        text.setText(Html.fromHtml(item.text));

        return inflate;
    }

    private String formatAmount(String amount) {
        final double asDouble = Double.parseDouble(amount);
        return String.format("%.2f",asDouble) + "€";

    }
}
