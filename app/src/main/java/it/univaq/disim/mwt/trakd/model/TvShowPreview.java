package it.univaq.disim.mwt.trakd.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity(tableName = "tv_shows")
public class TvShowPreview implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Expose
    private long _id;
    @Expose
    private long tv_show_id;
    @Expose
    private String name;
    @Expose
    private String poster_path;

    @Ignore
    public static final Creator<TvShowPreview> CREATOR = new Creator<TvShowPreview>() {
        @Override
        public TvShowPreview createFromParcel(Parcel source) {
            return new TvShowPreview(source);
        }

        @Override
        public TvShowPreview[] newArray(int size) {
            return new TvShowPreview[size];
        }
    };

    public TvShowPreview() {
    }

    @Ignore
    public TvShowPreview(long tv_show_id, String name, String poster_path) {
        this.tv_show_id = tv_show_id;
        this.name = name;
        this.poster_path = poster_path;
    }

    @Ignore
    public TvShowPreview(Parcel source) {
        _id = source.readLong();
        tv_show_id = source.readLong();
        name = source.readString();
        poster_path = source.readString();
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getTv_show_id() {
        return tv_show_id;
    }

    public void setTv_show_id(long tv_show_id) {
        this.tv_show_id = tv_show_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeLong(tv_show_id);
        dest.writeString(name);
        dest.writeString(poster_path);
    }
}
