package ir.esfandune.database;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.esfandune.doaahd.Act_Main;
import ir.esfandune.doaahd.R;

public class Rc_doaAdapter extends RecyclerView.Adapter<Rc_doaAdapter.ViewHolder> {

    private Act_Main act_main;
    private RotateAnimation rotateAnimation;
    private boolean isDay;
    private List<Doa> mValues;

    public Rc_doaAdapter(Act_Main act_main, List<Doa> items) {
        mValues = items;
        this.act_main = act_main;
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(5000);
    }


    @NonNull
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
        if (Act_Main.PlayingItemPost == position /*&& act_main.mp.isPlaying()*/) {
            holder.itm_arabi.setTextColor(getAccent());
            holder.itm_img.startAnimation(rotateAnimation);
        }else {
            holder.itm_arabi.setTextColor(isDay ? Color.BLACK : Color.WHITE);
            holder.itm_img.clearAnimation();
        }
    }

    private int getAccent() {
        TypedValue typedValue = new TypedValue();
        act_main.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
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

    class ViewHolder extends RecyclerView.ViewHolder {
          View mView;
          TextView itm_arabi, itm_farsi;
         ImageView itm_img;
        CardView card;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            card = view.findViewById(R.id.card);
            itm_arabi = view.findViewById(R.id.itm_arabi);
            itm_farsi = view.findViewById(R.id.itm_farsi);
            itm_img = view.findViewById(R.id.itm_img);


            SharedPreferences sh = act_main.getSharedPreferences("setting", 0);
            Typeface font = Typeface.createFromAsset(act_main.getAssets(), sh.getString("font_type", "IRANSansMobile(FaNum).ttf"));
            Typeface byekan = Typeface.createFromAsset(act_main.getAssets(), "IRANSansMobile(FaNum).ttf");
            int size = sh.getInt("font_size", 12) + 8;

            itm_arabi.setTypeface(font);
            itm_arabi.setTextSize(size);
            itm_farsi.setTypeface(byekan);


            isDay = sh.getInt("theme", R.style.AppBaseTheme) == R.style.AppBaseTheme;
            itm_img.setImageResource(isDay ? R.drawable.slim_day : R.drawable.slim_night);
            card.setCardBackgroundColor(isDay ? act_main.getResources().getColor(R.color.itm_d) : act_main.getResources().getColor(R.color.itm_n));
//            itm_arabi.setTextColor(isDay?Color.BLACK:Color.WHITE);


        }


    }

}
