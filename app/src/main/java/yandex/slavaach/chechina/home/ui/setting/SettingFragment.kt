package yandex.slavaach.chechina.home.ui.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import yandex.slavaach.chechina.home.R
import yandex.slavaach.chechina.home.provider.ViewModelFactory

class SettingFragment : Fragment() {



    private var settingViewModel: SettingViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //ViewModelProvider(this,ViewModelFactory(ItemListUseCase())).get(MainViewModel::class.java)
        settingViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        val loginView = root.findViewById<TextView>(R.id.etUsername)
        val passwordView = root.findViewById<TextView>(R.id.etPassword)
        val repidPasswordView = root.findViewById<TextView>(R.id.repitPassword)
        loginView.text = settingViewModel!!.getLogin()!!.value
        passwordView.text = settingViewModel!!.getPassword()!!.value
        repidPasswordView.text = settingViewModel!!.getRepidPassword()!!.value

        loginView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                 settingViewModel!!.setLogin(loginView.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        passwordView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                settingViewModel!!.settPassword(passwordView.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        repidPasswordView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                settingViewModel!!.setRepidPassword(repidPasswordView.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })



        (root.findViewById<TextView>(R.id.btRegister) as Button).setOnClickListener {
            settingViewModel!!.Save()
        }

        return root
    }


}