package yandex.slavaach.chechina.home


import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import yandex.slavaach.chechina.home.provider.AppComponent
import yandex.slavaach.chechina.home.provider.AppModule
import yandex.slavaach.chechina.home.provider.DaggerAppComponent
import yandex.slavaach.chechina.home.provider.WeatherApi


class SupMultiDexApp : Application()   {
    var component: AppComponent? = null
    private var retrofit: Retrofit? = null
    private var weatherApi: WeatherApi? = null


    override fun onCreate() {
        super.onCreate()
        instance = this
        // init AppComponent on start of the Application
        component = DaggerAppComponent.builder()
                .appModule(AppModule(instance!!))
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(" https://api.openweathermap.org/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())//Конвертер, необходимый для преобразования JSON'а в объекты
                .build()
        weatherApi = retrofit!!.create(WeatherApi::class.java) //Создаем объект, при помощи которого будем выполнять запросы

    }

    fun getApi(): WeatherApi? {
        return weatherApi
    }

    companion object {
        var instance: SupMultiDexApp? = null
        protected set


    }
}
