package yandex.slavaach.chechina.home.ui.weather

import android.app.Application
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import yandex.slavaach.chechina.home.R
import yandex.slavaach.chechina.home.SupMultiDexApp
import yandex.slavaach.chechina.home.ui.map.MapProvider
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import javax.inject.Inject


class WeatherViewModel (application: Application) : AndroidViewModel(application),LifecycleOwner  {
    private val mTextHome: MutableLiveData<String>
    private val mTextCity: MutableLiveData<String>
    private var mCity: MutableLiveData<String>

    @Inject
    lateinit var mapProvider: MapProvider

    var mApplication: Application
    val textHome: LiveData<String>
        get() = mTextHome

    val textCity: LiveData<String>
        get() = mTextCity

    private val mLifecycleRegistry: LifecycleRegistry


    init {
        mApplication = application
        mTextHome = MutableLiveData()
        mTextCity = MutableLiveData()
        mCity = MutableLiveData()
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED)
        (mApplication as  SupMultiDexApp).component!!.inject(this)

        mapProvider.locationHome.observe(this, Observer { l -> viewHome(l) })



    }
    fun setCity(city:String)  {
        mCity.value = city
    }
    fun viewHome(location: Location){
        try {

            GlobalScope.launch(Dispatchers.IO) {


                val weatherKey = mApplication.resources.getString(R.string.weather_key)
                var response: Response<ResponseBody>? = null
                try{
                    response = (mApplication as SupMultiDexApp).getApi()!!.getLatLonData(location.latitude.toString()
                            , location.longitude.toString(), "ru",  weatherKey).execute()

                }catch (e:Exception){
                    e.printStackTrace()
                }
                response?.let {res ->
                    res.body()!!.byteStream().toString()
                    val strResponse = String(res.body()!!.byteStream().readBytes() , StandardCharsets.UTF_8)
                    var jsonObject  = Gson().fromJson(strResponse, JsonObject::class.java)
                    val jsonArray: JsonArray = jsonObject!!.getAsJsonArray("weather")

                    val arrName: Array<JsonObject> = Gson().fromJson(jsonArray, Array<JsonObject>::class.java)
                    val description = arrName[0].get("description").asString


                    withContext(Dispatchers.Main) {
                        mTextHome.value = "Дома: " + description
                    }
                }

            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun viewCity(){
        if(mCity.value.isNullOrEmpty()) {
            Toast.makeText(getApplication(), "город пуст", Toast.LENGTH_SHORT).show()
            return
        }
        if(Pattern.compile("[а-яА-Я]").matcher(mCity.value).find()){
            Toast.makeText(getApplication(), "город надо по латыне", Toast.LENGTH_SHORT).show()
            return
        }
        try {

            GlobalScope.launch(Dispatchers.IO) {


                val weatherKey = mApplication.resources.getString(R.string.weather_key)
                var response: Response<ResponseBody>? = null
                try{
                    response = (mApplication as SupMultiDexApp).getApi()!!.getCityData(mCity.value
                            , "ru",  weatherKey).execute()

                }catch (e:Exception){
                    e.printStackTrace()
                }
                response?.let {res ->
                    res.body()!!.byteStream().toString()
                    val strResponse = String(res.body()!!.byteStream().readBytes() , StandardCharsets.UTF_8)
                    var jsonObject  = Gson().fromJson(strResponse, JsonObject::class.java)
                    var ds ="не получилось узнать"
                    jsonObject?.let {
                        val jsonArray: JsonArray = jsonObject!!.getAsJsonArray("list")
                        if(it.get("count").asInt != 0) {

                            val arrName: Array<JsonObject> = Gson().fromJson(jsonArray, Array<JsonObject>::class.java)
                            val arrWr: Array<JsonObject> = Gson().fromJson(arrName[0].get("weather"), Array<JsonObject>::class.java)
                            ds = arrWr[0].get("description").asString
                            //Log.e("PROFILE", arrWr[0].toString())
                        }
                    }
                    val description = ds
                    withContext(Dispatchers.Main) {
                        mTextCity.value = description
                    }
                }

            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    @NonNull
    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

}