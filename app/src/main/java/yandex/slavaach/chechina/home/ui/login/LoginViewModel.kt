package yandex.slavaach.chechina.home.ui.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import yandex.slavaach.chechina.home.MainActivity
import yandex.slavaach.chechina.home.ui.setting.KEY_LOGIN
import yandex.slavaach.chechina.home.ui.setting.KEY_PASSWORD

val NEW_LOGIN = "test"
val NEW_PASSWORD = "Test123*"

@Module
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var mApplication: Application

    private var mLogin: MutableLiveData<String>
    private var mPassword: MutableLiveData<String>
    private var mLoad: MutableLiveData<Boolean>

    val load: LiveData<Boolean>
        get() = mLoad

    private val mPasswordCorrect:String
    private val mLoginCorrect:String

    init  {
        mApplication = application
        mLogin = MutableLiveData()
        mPassword = MutableLiveData()
        mLoad = MutableLiveData()
        if (mApplication.getSharedPreferences(KEY_PASSWORD, Context.MODE_PRIVATE).getString(KEY_PASSWORD , "").isNullOrBlank()){
            mLogin.value = NEW_LOGIN
            mPasswordCorrect = NEW_PASSWORD
            } else {

            mLogin.value = mApplication.getSharedPreferences(KEY_LOGIN, Context.MODE_PRIVATE).getString(KEY_LOGIN, "")
            mPasswordCorrect = mApplication.getSharedPreferences(KEY_PASSWORD, Context.MODE_PRIVATE).getString(KEY_PASSWORD, "")!!
        }
        mLoginCorrect = mLogin.value.orEmpty()

    }

    fun getPassword(): LiveData<String>? {
        return mPassword
    }

    fun getLogin(): LiveData<String>? {
        return mLogin
    }



    fun settPassword(password:String)  {
        mPassword.value = password
    }

    fun setLogin(login:String) {
        mLogin.value = login
    }
    fun Save(){
        if(mPassword.value.isNullOrEmpty() || mLogin.value.isNullOrEmpty() ) {
            Toast.makeText(getApplication(), "Пароль или логин некорректны", Toast.LENGTH_SHORT).show()
            return
        }
        if(mPassword.value.equals(mPasswordCorrect) && mLogin.value.equals(mLoginCorrect)){
            mLoad.value = true

        }
        else {
            Toast.makeText(getApplication(), "Пароль или логин некорректны", Toast.LENGTH_SHORT).show()
        }






    }

}
