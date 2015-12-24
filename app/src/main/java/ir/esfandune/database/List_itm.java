
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package ir.esfandune.database ;


import android.os.Parcel;
import android.os.Parcelable;

public class List_itm implements Parcelable {

	private int id;
	private String title;
	private String img;
	private String ezafi;


	public List_itm() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String gettitle() {
		return title;
	}

	public void settitle(String title) {
		this.title = title;
	}

	// ///

	public String getimg() {
		return img;
	}

	public void setimg(String img) {
		this.img = img;
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
		return title + "\n(" + img + ")";

	}

	public List_itm(Parcel in) {
		// Log.i(DBOpenHelper.LOGTAG, "Parcel constructor");

		id = in.readInt();
		title= in.readString();
		img= in.readString();
		ezafi= in.readString();
		
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeString(img);
		dest.writeString(ezafi);
		
			
		
		
	}

	public static final Parcelable.Creator<List_itm> CREATOR = new Parcelable.Creator<List_itm>() {

		@Override
		public List_itm createFromParcel(Parcel source) {
			// Log.i(DBOpenHelper.LOGTAG, "createFromParcel");
			return new List_itm(source);
		}

		@Override
		public List_itm[] newArray(int size) {
			// Log.i(DBOpenHelper.LOGTAG, "newArray");
			return new List_itm[size];
		}

	};
}
