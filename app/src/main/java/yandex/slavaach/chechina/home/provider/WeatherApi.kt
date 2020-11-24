package yandex.slavaach.chechina.home.provider

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("/data/2.5/weather")
    fun getLatLonData(@Query("lat") lat: String?,
                      @Query("lon") lon: String?,
                      @Query("lang") lang:String = "ru",
                      @Query("APPID") key: String?): Call<ResponseBody>

    @GET("/data/2.5/find")
    fun getCityData(@Query("q") city: String?,
                      @Query("lang") lang:String = "ru",
                      @Query("APPID") key: String?): Call<ResponseBody>


}