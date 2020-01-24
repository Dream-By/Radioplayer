package com.example.radiogomelfm;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicInterface {

    //@GET("/2.0/")
    //Call <ArtistInfo> getArtistInfoMusic (@Query("method") String method, @Query("artist") String artist, @Query("api_key") String API_KEY, @Query("format") String format);

    @GET("/2.0/")
    Call <TrackInfo> getTrackInfo (@Query("method") String method, @Query("api_key") String API_KEY, @Query("artist") String artist, @Query("track") String track, @Query("format") String format);

	
	@GET("/2.0/")
    Call <String> getArtistInfoForDebug(@Query("method") String method, @Query("artist") String artist, @Query("api_key") String API_KEY, @Query("format") String format);

    @GET("/2.0/")
    Call <String> getAlbumInfoForDebug(@Query("method") String method, @Query("api_key") String API_KEY, @Query("artist") String artist, @Query("track") String track, @Query("format") String format);
}
