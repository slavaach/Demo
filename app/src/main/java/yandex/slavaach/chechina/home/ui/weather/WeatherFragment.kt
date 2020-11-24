package yandex.slavaach.chechina.home.ui.weather

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import yandex.slavaach.chechina.home.R
import yandex.slavaach.chechina.home.provider.ViewModelFactory

class WeatherFragment : Fragment() {
    private var weatherViewModel: WeatherViewModel? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        weatherViewModel = ViewModelProvider(this,  ViewModelFactory(requireActivity().application)).get(WeatherViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_weather, container, false)
        val textViewHome = root.findViewById<TextView>(R.id.text_weather_home)
        val textViewCity = root.findViewById<TextView>(R.id.text_weather_city)
        val textCity = root.findViewById<TextView>(R.id.etCity)
        weatherViewModel!!.textHome.observe(viewLifecycleOwner, Observer { s -> textViewHome.text = s })
        weatherViewModel!!.textCity.observe(viewLifecycleOwner, Observer { s -> textViewCity.text = s })
        textCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                weatherViewModel!!.setCity(textCity.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                weatherViewModel!!.setCity(textCity.text.toString())
            }
        })

        (root.findViewById<TextView>(R.id.btRegister) as Button).setOnClickListener {
            weatherViewModel!!.viewCity()
        }

        return root
    }
}