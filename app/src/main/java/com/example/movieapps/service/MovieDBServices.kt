package com.example.movieapps.service

import com.example.movieapps.model.NowPlaying
import com.example.movieapps.model.RawMovie
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDBServices { // PENAMAAN HARUS --> nama API . 'Services'

    // dari https://api.themoviedb.org/3/movie/now_playing dan harus mirip
    @GET("now_playing")

    // query params, diisi pake default yg di web nya
    // nggak bisa return List/array, soalnya APInya ngereturn object, jadi perlu dibikin model
    suspend fun getAllMovie(@Query("page") page: Int = 1, @Query("language") language: String = "en-US"): NowPlaying

    @GET("{movie_id}") // Query atau Path tergantung dari webnya
    suspend fun getMovieDetail(@Path("movie_id") movie_id: Int): RawMovie
}