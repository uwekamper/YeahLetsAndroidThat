package org.c_base.yeahletsdothat;

import android.app.Activity;
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
        TextView tv = new TextView(context);
        tv.setText(getItem(position).title);
        return tv;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
