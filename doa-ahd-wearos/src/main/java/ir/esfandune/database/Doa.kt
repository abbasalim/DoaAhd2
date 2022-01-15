// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 
package ir.esfandune.database

import android.os.Parcel
import android.os.Parcelable

class Doa : Parcelable {
    var id = 0
    private var arabi: String? = null
    private var farsi: String? = null
    private var ezafi: String? = null

    constructor() {}
    constructor(`in`: Parcel) {
        // Log.i(DBOpenHelper.LOGTAG, "Parcel constructor");
        id = `in`.readInt()
        arabi = `in`.readString()
        farsi = `in`.readString()
        ezafi = `in`.readString()
    }

    // ///
    fun getarabi(): String? {
        return arabi
    }

    fun setarabi(arabi: String?) {
        this.arabi = arabi
    }

    fun getfarsi(): String? {
        return farsi
    }

    fun setfarsi(farsi: String?) {
        this.farsi = farsi
    }

    //
    //
    fun getezafi(): String? {
        return ezafi
    }

    fun setezafi(ezafi: String?) {
        this.ezafi = ezafi
    }

    override fun toString(): String {
        // TODO Auto-generated method stub
        return "$arabi\n($farsi)"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(arabi)
        dest.writeString(farsi)
        dest.writeString(ezafi)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Doa?> = object : Parcelable.Creator<Doa?> {
            override fun createFromParcel(source: Parcel): Doa? {
                // Log.i(DBOpenHelper.LOGTAG, "createFromParcel");
                return Doa(source)
            }

            override fun newArray(size: Int): Array<Doa?> {
                // Log.i(DBOpenHelper.LOGTAG, "newArray");
                return arrayOfNulls(size)
            }
        }
    }
}