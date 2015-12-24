
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package ir.esfandune.database ;


import android.os.Parcel;
import android.os.Parcelable;

public class Doa implements Parcelable {

	private int id;
	private String arabi;
	private String farsi;
	private String ezafi;


	public Doa() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getarabi() {
		return arabi;
	}

	public void setarabi(String arabi) {
		this.arabi = arabi;
	}

	// ///

	public String getfarsi() {
		return farsi;
	}

	public void setfarsi(String farsi) {
		this.farsi = farsi;
	}

//
	public String getezafi() {
		return ezafi;
	}

	public void setezafi(String ezafi) {
		this.ezafi = ezafi;
	}
	
	//

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return arabi + "\n(" + farsi + ")";

	}

	public Doa(Parcel in) {
		// Log.i(DBOpenHelper.LOGTAG, "Parcel constructor");

		id = in.readInt();
		arabi= in.readString();
		farsi= in.readString();
		ezafi= in.readString();
		
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(arabi);
		dest.writeString(farsi);
		dest.writeString(ezafi);
		
			
		
		
	}

	public static final Parcelable.Creator<Doa> CREATOR = new Parcelable.Creator<Doa>() {

		@Override
		public Doa createFromParcel(Parcel source) {
			// Log.i(DBOpenHelper.LOGTAG, "createFromParcel");
			return new Doa(source);
		}

		@Override
		public Doa[] newArray(int size) {
			// Log.i(DBOpenHelper.LOGTAG, "newArray");
			return new Doa[size];
		}

	};
}
