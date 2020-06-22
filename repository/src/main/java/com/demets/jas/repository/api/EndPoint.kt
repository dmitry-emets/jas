package com.demets.jas.repository.api

/**
 * Created by dmitry on 23/12/2017.
 */
object EndPoint {

    //Album
    const val ALBUM_ADD_TAGS = "?method=album.addTags"
    const val ALBUM_GET_INFO = "?method=album.getinfo"
    const val ALBUM_GET_TAGS = "?method=album.gettags"
    const val ALBUM_GET_TOP_TAGS = "?method=album.gettoptags"
    const val ALBUM_REMOVE_TAG = "?method=album.removeTag"
    const val ALBUM_SEARCH = "?method=album.search"

    //Artist
    const val ARTIST_ADD_TAGS = "?method=artist.addTags"
    const val ARTIST_GET_CORRECTION = "?method=artist.getcorrection"
    const val ARTIST_GET_INFO = "?method=artist.getinfo"
    const val ARTIST_GET_SIMILAR = "?method=artist.getsimilar"
    const val ARTIST_GET_TAGS = "?method=artist.getTags"
    const val ARTIST_GET_TOP_ALBUMS = "?method=artist.gettopalbums"
    const val ARTIST_GET_TOP_TAGS = "?method=artist.gettoptags"
    const val ARTIST_GET_TOP_TRACKS = "?method=artist.gettoptracks"
    const val ARTIST_REMOVE_TAG = "?method=artist.removeTag"
    const val ARTIST_SEARCH = "?method=artist.search"

    //Auth
    const val AUTH_GET_SESSION = "?method=auth.getSession"
    const val AUTH_GET_TOKEN = "?method=auth.getToken"

    //Chart
    const val CHART_GET_TOP_ARTISTS = "?method=chart.gettopartists"
    const val CHART_GET_TOP_TAGS = "?method=chart.gettoptags"
    const val CHART_GET_TOP_TRACKS = "?method=chart.gettoptracks"

    //Geo
    const val GEO_GET_TOP_ARTISTS = "?method=geo.gettopartists"
    const val GEO_GET_TOP_TRACKS = "?method=geo.gettoptracks"

    //Library
    const val LIBRARY_GET_ARTISTS = "?method=library.getartists"

    //Tag
    const val TAG_GET_INFO = "?method=tag.getinfo"
    const val TAG_GET_SIMILAR = "?method=tag.getsimilar"
    const val TAG_GET_TOP_ALBUMS = "?method=tag.gettopalbums"
    const val TAG_GET_TOP_ARTISTS = "?method=tag.gettopartists"
    const val TAG_GET_TOP_TAGS = "?method=tag.getTopTags"
    const val TAG_GET_TOP_TRACKS = "?method=artist.gettoptracks"
    const val TAG_GET_WEEKLY_CHART_LIST = "?method=tag.getweeklychartlist"

    //Track
    const val TRACK_ADD_TAGS = "?method=track.addTags"
    const val TRACK_GET_CORRECTION = "?method=track.getcorrection"
    const val TRACK_GET_INFO = "?method=track.getInfo"
    const val TRACK_GET_SIMILAR = "?method=track.getsimilar"
    const val TRACK_GET_TAGS = "?method=track.getTags"
    const val TRACK_GET_TOP_TAGS = "?method=track.gettoptags"
    const val TRACK_LOVE = "?method=track.love"
    const val TRACK_REMOVE_TAG = "?method=track.removeTag"
    const val TRACK_SCROBBLE = "?method=track.scrobble"
    const val TRACK_SEARCH = "?method=track.search"
    const val TRACK_UNLOVE = "?method=track.unlove"
    const val TRACK_UPDATE_NOW_PLAYING = "?method=track.updateNowPlaying"

    //User
    const val USER_GET_ARTIST_TRACKS = "?method=user.getartisttracks"
    const val USER_GET_FRIENDS = "?method=user.getfriends"
    const val USER_GET_INFO = "?method=user.getinfo"
    const val USER_GET_LOVED_TRACKS = "?method=user.getlovedtracks"
    const val USER_GET_PERSONAL_TAGS = "?method=user.getpersonaltags"
    const val USER_GET_RECENT_TRACKS = "?method=user.getrecenttracks"
    const val USER_GET_TOP_ALBUMS = "?method=user.gettopalbums"
    const val USER_GET_TOP_ARTISTS = "?method=user.gettopartists"
    const val USER_GET_TOP_TAGS = "?method=user.gettoptags"
    const val USER_GET_TOP_TRACKS = "?method=user.gettoptracks"
    const val USER_GET_WEEKLY_ALBUM_CHART = "?method=user.getweeklyalbumchart"
    const val USER_GET_WEEKLY_ARTIST_CHART = "?method=user.getweeklyartistchart"
    const val USER_GET_WEEKLY_CHART_LIST = "?method=user.getweeklychartlist"
    const val USER_GET_WEEKLY_TRACK_CHART = "?method=user.getweeklytrackchart"
}