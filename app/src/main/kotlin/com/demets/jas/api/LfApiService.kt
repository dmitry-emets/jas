package com.demets.jas.api

import com.demets.jas.api.model.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.math.BigInteger
import java.security.MessageDigest


/**
 * Created by dmitry on 26/11/2017.
 */


interface LfApiService {
    //Album Queries
    //TODO addTags, removeTag

    /**
     * Get the metadata and tracklist for an album on Last.fm using the album name or a musicbrainz id.
     *
     * @param artist        (Required (unless mbid)) : The artist name.
     * @param album         (Required (unless mbid)) : The album name.
     * @param mbid          (Optional) : The musicbrainz id for the album.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will
     *                      be returned in the response.
     * @param username      (Optional) : The username for the context of the request. If supplied,
     *                      the user's playcount for this album is included in the response.
     * @param lang          (Optional) : The language to return the biography in, expressed as an
     *                      ISO 639 alpha-2 code.
     */
    @GET(EndPoint.ALBUM_GET_INFO)
    fun getAlbumInfo(@Query("artist") artist: String,
                     @Query("album") album: String,
                     @Query("mbid") mbid: String? = null,
                     @Query("autocorrect") autocorrect: Int = 1,
                     @Query("username") username: String? = null,
                     @Query("lang") lang: String? = null): Observable<Response<Album>>

    /**
     * Get the tags applied by an individual user to an album on Last.fm.
     * To retrieve the list of top tags applied to an album by all users use [getAlbumTopTags].
     *
     * @param artist        (Required (unless mbid)) : The artist name.
     * @param album         (Required (unless mbid)) : The album name.
     * @param mbid          (Optional) : The musicbrainz id for the album.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will
     *                      be returned in the response.
     * @param user          If called in non-authenticated mode you must specify the user to look up.
     */
    @GET(EndPoint.ALBUM_GET_TAGS)
    fun getAlbumTags(@Query("artist") artist: String,
                     @Query("album") album: String,
                     @Query("mbid") mbid: String? = null,
                     @Query("autocorrect") autocorrect: Int = 1,
                     @Query("user") user: String? = null): Observable<Response<TagListWrapper>>

    /**
     * Get the top tags for an album on Last.fm, ordered by popularity.
     *
     * @param artist        (Required (unless mbid)) : The artist name.
     * @param album         (Required (unless mbid)) : The album name.
     * @param mbid          (Optional) : The musicbrainz id for the album.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will
     *                      be returned in the response.
     */
    @GET(EndPoint.ALBUM_GET_TOP_TAGS)
    fun getAlbumTopTags(@Query("artist") artist: String,
                        @Query("album") album: String,
                        @Query("mbid") mbid: String? = null,
                        @Query("autocorrect") autocorrect: Int = 1): Observable<Response<TagListWrapper>>

    /**
     * Search for an album by name. Returns album matches sorted by relevance.
     *
     * @param album         (Required) : The album name.
     * @param limit         (Optional) : The number of results to fetch per page. Defaults to 30.
     * @param page          (Optional) : The page number to fetch. Defaults to first page.
     */
    @GET(EndPoint.ALBUM_SEARCH)
    fun searchAlbum(@Query("album") album: String,
                    @Query("limit") limit: Int? = null,
                    @Query("page") page: Int? = null): Observable<Response<Search<Album>>>

    //Artist Queries
    //TODO addTags, removeTag

    /**
     * Use the last.fm corrections data to check whether the supplied artist has a correction
     * to a canonical artist.
     *
     * @param artist         (Required) : The artist name to correct.
     */
    @GET(EndPoint.ARTIST_GET_CORRECTION)
    fun getArtistCorrection(@Query("artist") artist: String): Observable<Response<Artist>>
    //TODO: realise correction model.

    /**
     * Get the metadata for an artist. Includes biography, truncated at 300 characters.
     *
     * @param artist        (Required (unless mbid)] : The artist name.
     * @param mbid          (Optional) : The musicbrainz id for the artist.
     * @param lang          (Optional) : The language to return the biography in, expressed as an
     *                      ISO 639 alpha-2 code.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will
     *                      be returned in the response.
     * @param username      (Optional) : The username for the context of the request. If supplied,
     *                      the user's playcount for this artist is included in the response.
     */
    @GET(EndPoint.ARTIST_GET_INFO)
    fun getArtistInfo(@Query("artist") artist: String,
                      @Query("mbid") mbid: String? = null,
                      @Query("lang") lang: String? = null,
                      @Query("autocorrect") autocorrect: Int = 1,
                      @Query("username") username: String? = null): Observable<Response<Artist>>

    /**
     * Get all the artists similar to this artist.
     *
     * @param artist        (Required (unless mbid)] : The artist name.
     * @param mbid          (Optional) : The musicbrainz id for the artist.
     * @param limit         (Optional) : Limit the number of similar artists returned.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will
     *                      be returned in the response.
     */
    @GET(EndPoint.ARTIST_GET_SIMILAR)
    fun getSimilarArtists(@Query("artist") artist: String,
                          @Query("mbid") mbid: String? = null,
                          @Query("limit") limit: Int? = null,
                          @Query("autocorrect") autocorrect: Int = 1): Observable<Response<ArtistListWrapper>>

    /**
     * Get the tags applied by an individual user to an artist on Last.fm. If accessed as an
     * authenticated service /and/ you don't supply a user parameter then this service will
     * return tags for the authenticated user.
     * To retrieve the list of top tags applied to an artist by all users use [getArtistTopTags].
     *
     * @param artist        (Required (unless mbid)) : The artist name.
     * @param mbid          (Optional) : The musicbrainz id for the artist.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will be
     *                      returned in the response.
     * @param user          If called in non-authenticated mode you must specify the user to look up.
     */
    @GET(EndPoint.ARTIST_GET_TAGS)
    fun getArtistTags(@Query("artist") artist: String,
                      @Query("mbid") mbid: String? = null,
                      @Query("autocorrect") autocorrect: Int = 1,
                      @Query("user") user: String? = null): Observable<Response<TagListWrapper>>

    /**
     * Get the top albums for an artist on Last.fm, ordered by popularity.
     *
     * @param artist        (Required (unless mbid)) : The artist name.
     * @param mbid          (Optional) : The musicbrainz id for the artist.
     * @param autocorrect   (Optional) : Transform misspelled artist names into correct artist names,
     *                      returning the correct version instead. The corrected artist name will be
     *                      returned in the response.
     * @param page          (Optional) : The page number to fetch. Defaults to first page.
     * @param limit         (Optional) : The number of results to fetch per page. Defaults to 50.
     *
     */
    @GET(EndPoint.ARTIST_GET_TOP_ALBUMS)
    fun getArtistTopAlbums(@Query("artist") artist: String,
                           @Query("mbid") mbid: String? = null,
                           @Query("autocorrect") autocorrect: Int = 1,
                           @Query("page") page: Int? = null,
                           @Query("limit") limit: Int? = null): Observable<Response<AlbumListWrapper>>

    //Auth Queries

    /**
     * Fetch an unathorized request token for an API account.
     * api_key and api_sig query parameters will be added automatically by retrofit interceptor.
     *
     */
    @GET(EndPoint.AUTH_GET_TOKEN)
    fun getAuthToken(): Observable<Response<String>>

    /**
     * Fetch a session key for a user.
     * api_key and api_sig query parameters will be added automatically by retrofit interceptor.
     *
     * @param token (Required) : A 32-character ASCII hexadecimal MD5 hash returned by step 1 of
     *              the authentication process (following the granting of permissions to the application by the user)
     *
     */
    @GET(EndPoint.AUTH_GET_SESSION)
    fun getAuthSession(@Query("token") token: String): Observable<Response<Session>>

    //Chart Queries

    /**
     * Get the top artists chart.
     *
     * @param page  (Optional) : The page number to fetch. Defaults to first page.
     * @param limit (Optional) : The number of results to fetch per page. Defaults to 50.
     */
    @GET(EndPoint.CHART_GET_TOP_ARTISTS)
    fun getChartTopArtists(@Query("page") page: Int? = null,
                           @Query("limit") limit: Int? = null): Observable<Response<ArtistListWrapper>>

    /**
     * Get the top tags chart.
     *
     * @param page  (Optional) : The page number to fetch. Defaults to first page.
     * @param limit (Optional) : The number of results to fetch per page. Defaults to 50.
     */
    @GET(EndPoint.CHART_GET_TOP_TAGS)
    fun getChartTopTags(@Query("page") page: Int? = null,
                        @Query("limit") limit: Int? = null): Observable<Response<TagListWrapper>>

    /**
     * Get the top tracks chart.
     *
     * @param page  (Optional) : The page number to fetch. Defaults to first page.
     * @param limit (Optional) : The number of results to fetch per page. Defaults to 50.
     */
    @GET(EndPoint.CHART_GET_TOP_TRACKS)
    fun getChartTopTracks(@Query("page") page: Int? = null,
                          @Query("limit") limit: Int? = null): Observable<Response<TrackListWrapper>>

    //Geo Queries

    /**
     * Get the most popular artists on Last.fm by country.
     *
     * @param country   (Required) : A country name, as defined by the ISO 3166-1 country names standard.
     * @param page      (Optional) : The page number to fetch. Defaults to first page.
     * @param limit     (Optional) : The number of results to fetch per page. Defaults to 50.
     */
    @GET(EndPoint.GEO_GET_TOP_ARTISTS)
    fun getGeoTopArtists(@Query("country") country: String,
                         @Query("page") page: Int? = null,
                         @Query("limit") limit: Int? = null): Observable<Response<ArtistListWrapper>>

    /**
     * Get the most popular tracks on Last.fm last week by country.
     *
     * @param country   (Required) : A country name, as defined by the ISO 3166-1 country names standard.
     * @param location  (Optional) : A metro name, to fetch the charts for (must be within the country specified).
     * @param page      (Optional) : The page number to fetch. Defaults to first page.
     * @param limit     (Optional) : The number of results to fetch per page. Defaults to 50.
     */
    @GET(EndPoint.GEO_GET_TOP_TRACKS)
    fun getGeoTopTracks(@Query("country") country: String,
                        @Query("location") location: String? = null,
                        @Query("page") page: Int? = null,
                        @Query("limit") limit: Int? = null): Observable<Response<TrackListWrapper>>

    //Library Queries

    /**
     * A paginated list of all the artists in a user's library, with play counts and tag counts.
     *
     * @param user  (Required) : The user whose library you want to fetch.
     * @param limit (Optional) : The number of results to fetch per page. Defaults to 50.
     * @param page  (Optional) : The page number you wish to scan to.
     */
    @GET(EndPoint.LIBRARY_GET_ARTISTS)
    fun getLibraryArtists(@Query("user") user: String,
                          @Query("page") page: Int? = null,
                          @Query("limit") limit: Int? = null): Observable<Response<ArtistListWrapper>>

    //User Queries

    @GET(EndPoint.USER_GET_INFO)
    fun getUserInfo(@Query("user") user: String): Observable<Response<User>>

    @GET(EndPoint.USER_GET_FRIENDS)
    fun getFriends(@Query("user") user: String,
                   @Query("limit") limit: Int? = null,
                   @Query("page") page: Int? = null,
                   @Query("recenttracks") recentTrack: Boolean = false): Observable<Response<UserCommons>>

    @POST(EndPoint.TRACK_SCROBBLE)
    fun trackScrobble(@QueryMap options: Map<String, String>,
                      @Query("sk") sessionKey: String
            //TODO: Add additional parameters!
    ): Observable<Any> //TODO: Create specific model class.

    @POST(EndPoint.TRACK_UPDATE_NOW_PLAYING)
    fun trackUpdateNowPlaying(@QueryMap options: Map<String, String>,
                              @Query("sk") sessionKey: String
            //TODO: Add additional parameters!
    ): Observable<Any> //TODO: Create specific model class.

    @POST(EndPoint.TRACK_LOVE)
    fun trackLove(@Query("track") track: String,
                  @Query("artist") artist: String,
                  @Query("sk") sessionKey: String
    ): Observable<Any>

    @POST(EndPoint.TRACK_UNLOVE)
    fun trackUnlove(@Query("track") track: String,
                    @Query("artist") artist: String,
                    @Query("sk") sessionKey: String
    ): Observable<Any>

    @GET(EndPoint.TRACK_GET_INFO)
    fun getTrackInfo(@Query("track") track: String,
                     @Query("artist") artist: String,
                     @Query("mbid") mbid: String? = null,
                     @Query("autocorrect") autocorrect: Int = 1,
                     @Query("username") user: String? = null): Observable<Response<Track>>

    companion object Factory {
        fun generateMd5(params: Map<String, String>): String {
            params.toSortedMap()
            val md = MessageDigest.getInstance("MD5")
            val string = params.toSortedMap()
                    .minus("format")
                    .toList()
                    .joinToString("", transform = { it.first + it.second }) + SECRET_KEY
            val number = BigInteger(1, md.digest(string.toByteArray()))
            var hashText = number.toString(16)
            while (hashText.length < 32) {
                hashText = "0" + hashText
            }
            return hashText
        }

        private const val API_KEY: String = "6cda0aac2410defc7d67360134b3e76a"
        private const val SECRET_KEY: String = "c5e247e13f09d7baf6161e2ae31e4e39"
        private const val ROOT_URL: String = "http://ws.audioscrobbler.com/2.0/"
        private const val FORMAT: String = "json"

        private val methodsWithSign = listOf(
                "auth.getSession",
                "auth.getToken",
                "track.scrobble",
                "track.updateNowPlaying",
                "track.love",
                "track.unlove"
        )

        private fun signRequired(query: String?): Boolean =
                query != null && methodsWithSign.any { query.contains(it) }

        fun create(): LfApiService {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
                    .addInterceptor({ chain ->
                        val originalRequest = chain.request()
                        val originalUrl = originalRequest.url()
                        var url = originalUrl.newBuilder()
                                .addQueryParameter("api_key", API_KEY)
                                .addQueryParameter("format", FORMAT)
                                .build()
                        val query = url.query()
                        println(query)
                        //Calculate and add api_sig for auth methods.
                        if (signRequired(query)) {
                            val params: MutableMap<String, String> = mutableMapOf()
                            for (i in 0 until url.querySize()) {
                                params[url.queryParameterName(i)] = url.queryParameterValue(i)
                            }
                            url = url.newBuilder()
                                    .addQueryParameter("api_sig", generateMd5(params))
                                    .build()
                        }
                        val request = originalRequest.newBuilder()
                                .url(url)
                                .build()
                        chain.proceed(request)
                    })
                    .addInterceptor(httpLoggingInterceptor)
                    .build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(ROOT_URL)
                    .client(httpClient)
                    .build()

            return retrofit.create(LfApiService::class.java)
        }
    }
}