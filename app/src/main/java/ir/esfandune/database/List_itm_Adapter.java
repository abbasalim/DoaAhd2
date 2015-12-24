package ir.esfandune.database;






import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ir.esfandune.doaahd.R;


public class List_itm_Adapter extends ArrayAdapter<List_itm> {
    List<List_itm> items;

    Context c;
    Typeface broya ;

    public List_itm_Adapter(Context c,List<List_itm> items) {
        super(c, android.R.id.content, items);
        this.c = c;
        this.items = items;

        broya = Typeface.createFromAsset(c.getAssets(), "BROYA_rg.TTF");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.list_item, null);
        final List_itm List_itm = items.get(position);

        TextView itm_arabi = (TextView) view.findViewById(R.id.itm_arabi);
        itm_arabi.setText(List_itm.gettitle());
        itm_arabi.setTypeface(broya);

        ImageView itm_img = (ImageView) view.findViewById(R.id.itm_img);
        int ImageResource;
        ImageResource = c.getResources().getIdentifier(List_itm.getimg(), "drawable", c.getPackageName());
        itm_img.setImageResource(ImageResource);


        return view;
    }



}


