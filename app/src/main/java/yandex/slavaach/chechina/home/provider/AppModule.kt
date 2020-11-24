package yandex.slavaach.chechina.home.provider


import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule( @get:Singleton
                 @get:Provides val appContext: Context){


}
