package ir.esfandune.database

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ir.esfandune.doaahd.Act_Main
import ir.esfandune.doaahd.R

class Rc_doaAdapter(private val act_main: Activity, private val mValues: List<Doa>) :
    RecyclerView.Adapter<Rc_doaAdapter.ViewHolder>() {
    var font: Typeface = Typeface.createFromAsset(act_main.assets, "IRANSansMobile(FaNum).ttf")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Doa = mValues[position]
        holder.itm_arabi.text = Doa.getarabi()
        holder.itm_farsi.text = Doa.getfarsi()
        if (position == mValues.size - 1) holder.itm_farsi.setTextColor(Color.parseColor("#DC553F")) else holder.itm_farsi.setTextColor(
            Color.parseColor("#ff59b4a5")
        )
        if (Act_Main.PlayingItemPost == position) holder.itm_arabi.setTextColor(accent) else holder.itm_arabi.setTextColor(
            Color.WHITE
        )
    }

    private val accent: Int
         get() {
            val typedValue = TypedValue()
            act_main.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            return typedValue.data
        }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(private var mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        var itm_arabi: TextView = mView.findViewById(R.id.itm_arabi)
        var itm_farsi: TextView = mView.findViewById(R.id.itm_farsi)
        var itm_img: ImageView = mView.findViewById(R.id.itm_img)
        var card: CardView = mView.findViewById(R.id.card)

        init {
            itm_arabi.typeface = font
            itm_farsi.typeface = font
            itm_img.setImageResource(R.drawable.slim_night)
            card.setCardBackgroundColor(act_main.resources.getColor(R.color.itm_n))
        }
    }

}