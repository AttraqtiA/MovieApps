package com.example.movieapps.repository

import com.example.movieapps.service.MovieDBServices
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(private val bearerToken: String) : Interceptor { // import yg okhttp3 yow
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $bearerToken")
            .build()
        return chain.proceed(request)
    }
}
class MovieDBAPIContainer { // ngurus aksebilitas ajah

    companion object {
        val BASE_IMG = "https://image.tmdb.org/t/p/w500"
    }
    // companion --> static di Kotlin

    private val BASE_URL = "https://api.themoviedb.org/3/movie/"
    private val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YTUzNjgxN2I1NjU0ZTljNmIwMzI4MzI4YjJlNTYxMCIsInN1YiI6IjY1Mzc0M2ZlODViMTA1MDBlNDI2NDViYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.H2u3YjYT8nBxxiQE12u_heKJVclBqKcI2PGA97JlB1A"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(ACCESS_TOKEN))
        .build()

    private val retrofit = Retrofit.Builder()
//        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType())) dulu
        .addConverterFactory(
            GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    private val retrofitService: MovieDBServices by lazy {
        retrofit.create(MovieDBServices::class.java)
    }

    val MovieDBRepositories: MovieDBRepositories by lazy {
        MovieDBRepositories(retrofitService)
    }
}