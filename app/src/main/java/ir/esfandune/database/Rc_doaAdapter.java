package ir.esfandune.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.esfandune.doaahd.Act_Main;
import ir.esfandune.doaahd.R;

public class Rc_doaAdapter extends RecyclerView.Adapter<Rc_doaAdapter.ViewHolder> {

    Context CONtext;
    RotateAnimation rotateAnimation;
    boolean isDay;
    private List<Doa> mValues;

    public Rc_doaAdapter(Context context, List<Doa> items) {
        mValues = items;
        CONtext = context;
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(3000);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Doa Doa = mValues.get(position);
        holder.itm_arabi.setText(Doa.getarabi());
        holder.itm_farsi.setText(Doa.getfarsi());
        if (position == mValues.size() - 1) holder.itm_farsi.setTextColor(Color.RED);
        else holder.itm_farsi.setTextColor(Color.parseColor("#ff59b4a5"));
        if (Act_Main.PlayingItemPost == position) {
            holder.itm_arabi.setTextColor(getAccent());
        } else {
            holder.itm_img.clearAnimation();
            holder.itm_arabi.setTextColor(isDay ? Color.BLACK : Color.WHITE);
        }
    }

    private int getAccent() {
        TypedValue typedValue = new TypedValue();
        CONtext.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView itm_arabi, itm_farsi;
        public ImageView itm_img;
        CardView card;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            card = (CardView) view.findViewById(R.id.card);
            itm_arabi = (TextView) view.findViewById(R.id.itm_arabi);
            itm_farsi = (TextView) view.findViewById(R.id.itm_farsi);
            itm_img = (ImageView) view.findViewById(R.id.itm_img);


            SharedPreferences sh = CONtext.getSharedPreferences("setting", 0);
            Typeface font = Typeface.createFromAsset(CONtext.getAssets(), sh.getString("font_type", "IRANSansMobile(FaNum).ttf"));
            Typeface byekan = Typeface.createFromAsset(CONtext.getAssets(), "IRANSansMobile(FaNum).ttf");
            int size = sh.getInt("font_size", 18) + 8;

            itm_arabi.setTypeface(font);
            itm_arabi.setTextSize(size);
            itm_farsi.setTypeface(byekan);


            isDay = sh.getInt("theme", R.style.AppBaseTheme) == R.style.AppBaseTheme;
            itm_img.setImageResource(isDay ? R.drawable.slim_day : R.drawable.slim_night);
            card.setCardBackgroundColor(isDay ? CONtext.getResources().getColor(R.color.itm_d) : CONtext.getResources().getColor(R.color.itm_n));
//            itm_arabi.setTextColor(isDay?Color.BLACK:Color.WHITE);


        }


    }

}
