package ir.esfandune.database;




import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ir.esfandune.doaahd.R;


public class DoaAdapter extends ArrayAdapter<Doa> {
    List<Doa> items;

    Context c;
    Typeface byekan ;
    Typeface font;
    int size;

    public DoaAdapter(Context c,List<Doa> items) {
        super(c, android.R.id.content, items);
        this.c = c;
        this.items = items;

        SharedPreferences sh = c.getSharedPreferences("setting", 0);
        font = Typeface.createFromAsset(c.getAssets(), sh.getString("font_type", "Otaha.TTF"));
        byekan = Typeface.createFromAsset(c.getAssets(), "BYEKAN.TTF");
        size = sh.getInt("font_size", 30);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view ;
        if (convertView == null){
             view = vi.inflate(R.layout.item, null);
        }else {
             view = convertView;
        }
        final Doa Doa = items.get(position);

        TextView textView = (TextView) view.findViewById(R.id.itm_arabi);
        textView.setTypeface(font);
        textView.setTextSize(size);
        textView.setText(Doa.getarabi());

        textView = (TextView) view.findViewById(R.id.itm_farsi);

        textView.setTypeface(byekan);
        textView.setText(Doa.getfarsi());
        if (position == items.size()-1) textView.setTextColor(Color.RED);
        else textView.setTextColor(Color.parseColor("#ff59b4a5"));

        if (Doa.getezafi().equals("1")){
            Animation animation = AnimationUtils.loadAnimation(c, R.anim.move_arumtond);
            view.startAnimation(animation);
            // view.setHasTransientState(true);
            Doa.setezafi("0");
        }




        return view;
    }



}


