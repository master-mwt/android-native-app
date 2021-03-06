package it.univaq.disim.mwt.trakd.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.univaq.disim.mwt.trakd.model.TvShowPreview;

@Dao
public interface TvShowPreviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void save(TvShowPreview... tvShowPreviews);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void save(List<TvShowPreview> tvShowPreviews);

    @Update
    public void update(TvShowPreview... tvShowPreviews);

    @Query("DELETE FROM tv_shows WHERE tv_show_id = :tv_show_id")
    public void deleteByTvShowID(long tv_show_id);

    @Query("DELETE FROM tv_shows")
    public void deleteAll();

    @Query("SELECT * FROM tv_shows ORDER BY name ASC")
    public List<TvShowPreview> findAll();

    @Query("SELECT * FROM tv_shows WHERE id = :id")
    public TvShowPreview findByID(long id);

    @Query("SELECT * FROM tv_shows WHERE tv_show_id = :tv_show_id")
    public TvShowPreview findByTvShowId(long tv_show_id);
}
