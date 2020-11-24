package yandex.slavaach.chechina.home.ui.setting

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import yandex.slavaach.chechina.home.ui.login.NEW_LOGIN
import java.util.regex.Pattern

val KEY_LOGIN = "LOGIN"
val KEY_PASSWORD = "PASSWORD"

@Module
class SettingViewModel(application: Application) : AndroidViewModel(application) {

    var mApplication: Application

    private var mLogin: MutableLiveData<String>
    private var mPassword: MutableLiveData<String>
    private var mRepidPassword: MutableLiveData<String>

    init  {
        mApplication = application
        mLogin = MutableLiveData()
        mPassword = MutableLiveData()
        mRepidPassword = MutableLiveData()
        if( mApplication.getSharedPreferences(KEY_PASSWORD, Context.MODE_PRIVATE).getString(KEY_PASSWORD , "").isNullOrBlank()){
            mLogin.value = NEW_LOGIN
        } else {
            mLogin.value = mApplication.getSharedPreferences(KEY_LOGIN, Context.MODE_PRIVATE).getString(KEY_LOGIN, "")

        }


    }

    fun getPassword(): LiveData<String>? {
        return mPassword
    }
    fun getRepidPassword(): LiveData<String>? {
        return mRepidPassword
    }
    fun getLogin(): LiveData<String>? {
        return mLogin
    }

    fun settPassword(password:String)  {
        mPassword.value = password
    }
    fun setRepidPassword(repidPassword:String)  {
        mRepidPassword.value = repidPassword
    }
    fun setLogin(login:String) {
        mLogin.value = login
    }
    fun Save(){
        if(mPassword.value.isNullOrEmpty()  ) {
            Toast.makeText(getApplication(), "Пароль пуст", Toast.LENGTH_SHORT).show()
            return
        }
         if(mPassword.value!!.equals(mRepidPassword.value)) {

             Pattern.compile("([A-Za-z0-9]{7})(.+\$)").matcher(mPassword.value!!).find()
             !Pattern.compile("[а-яА-Я]").matcher(mPassword.value!!).find()
             Pattern.compile("([a-z]{1})").matcher(mPassword.value!!).find()
             Pattern.compile("(\\W{1})").matcher(mPassword.value!!).find()
            if(Pattern.compile("([A-Za-z0-9]{7})(.+\$)").matcher(mPassword.value!!).find() &&
                    !Pattern.compile("[а-яА-Я]").matcher(mPassword.value!!).find() &&
                    Pattern.compile("([a-z]{1})").matcher(mPassword.value!!).find() &&
                    Pattern.compile("(\\W{1})").matcher(mPassword.value!!).find()
            ) {
                mApplication.getSharedPreferences(KEY_LOGIN, Context.MODE_PRIVATE).edit().putString(KEY_LOGIN, mLogin.value!!).apply()
                mApplication.getSharedPreferences(KEY_PASSWORD, Context.MODE_PRIVATE).edit().putString(KEY_PASSWORD ,  mPassword.value!!).apply()
                Toast.makeText(getApplication(), "сохранили", Toast.LENGTH_SHORT).show()
            }
             else{
                Toast.makeText(getApplication(), "Пароль не соответствует политике", Toast.LENGTH_SHORT).show()

            }


         } else
             Toast.makeText(getApplication(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()

    }

}
