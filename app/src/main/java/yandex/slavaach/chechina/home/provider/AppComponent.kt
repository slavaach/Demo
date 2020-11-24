package yandex.slavaach.chechina.home.provider


import dagger.Component
import yandex.slavaach.chechina.home.ui.map.MapFragment
import yandex.slavaach.chechina.home.ui.map.MapProvider
import yandex.slavaach.chechina.home.ui.setting.SettingViewModel
import yandex.slavaach.chechina.home.ui.weather.WeatherViewModel
import javax.inject.Singleton


@Component(modules = [AppModule::class , MapProvider::class , SettingViewModel::class ])
@Singleton
interface AppComponent {
    fun inject(mapFragment: MapFragment)
    fun inject(weatherViewModel: WeatherViewModel)

}
