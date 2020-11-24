package yandex.slavaach.chechina.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import yandex.slavaach.chechina.home.provider.ViewModelFactory
import yandex.slavaach.chechina.home.ui.login.LoginViewModel
import yandex.slavaach.chechina.home.ui.setting.SettingViewModel

class LoginActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(application)).get(LoginViewModel::class.java)

        val loginView = findViewById<TextView>(R.id.etUsername)
        val passwordView = findViewById<TextView>(R.id.etPassword)
        loginView.text = loginViewModel!!.getLogin()!!.value
        passwordView.text = loginViewModel!!.getPassword()!!.value

        loginView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginViewModel!!.setLogin(loginView.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        passwordView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loginViewModel!!.settPassword(passwordView.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        (findViewById<TextView>(R.id.btRegister) as Button).setOnClickListener {
            loginViewModel!!.Save()
        }
        loginViewModel!!.load.observe(this, Observer { s -> startActivity(Intent(this, MainActivity::class.java)) })

    }
}