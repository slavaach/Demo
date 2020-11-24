package yandex.slavaach.chechina.home.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import yandex.slavaach.chechina.home.R
import yandex.slavaach.chechina.home.SupMultiDexApp
import javax.inject.Inject


class MapFragment : Fragment() , OnMapReadyCallback {

    @Inject
    lateinit var mapProvider: MapProvider
    var mGoogleMap: GoogleMap? = null



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        (getActivity()?.application as  SupMultiDexApp).component!!.inject(this)
        val mMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapFragment.getMapAsync(this)
        mapProvider.locationHome.observe(viewLifecycleOwner, Observer { l ->
           if(mGoogleMap != null) mapProvider.UpdateMarker(mGoogleMap!!)
        })
        return root
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mapProvider.UpdateMarker(googleMap)
        mGoogleMap = googleMap
    }
    override fun onPause() {
        super.onPause()
        mapProvider.removeUpdatesLocationManager()
    }

}