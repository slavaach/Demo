package yandex.slavaach.chechina.home.ui.map


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yandex.slavaach.chechina.home.R
import java.io.IOException
import java.text.DecimalFormat
import javax.inject.Singleton

@Module
class MapProvider {

    var mContext: Context? = null
    var mPlacesWishTitle: MutableList<LatLngWishTitle> = ArrayList()
    var mLocation: Location? = null
    private var locationManager: LocationManager? = null
    private var locationListenerChanell: LocationListener? = null
    private var mWidth:Int = 0
    var mLocationLive: MutableLiveData<Location>
    val locationHome: LiveData<Location>
        get() = mLocationLive

    init{
        mLocationLive = MutableLiveData()
    }
    @Singleton
    @Provides
    fun getMapMarker(context: Context): MapProvider {
        mContext = context
        mPlacesWishTitle.add(LatLngWishTitle(LatLng(55.781336, 37.408776), "серебрянный бор"))
        mPlacesWishTitle.add(LatLngWishTitle(LatLng(55.8062532, 37.685508), "Сокольники"))
        mPlacesWishTitle.add(LatLngWishTitle(LatLng(55.639512, 37.502242), "Тропарево"))
        locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mWidth =  if( context.resources.displayMetrics.widthPixels > context.resources.displayMetrics.heightPixels)
            context.resources.displayMetrics.heightPixels else context.resources.displayMetrics.widthPixels
        locationListenerChanell = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val pr = this
                mLocation = location
                TakeDistDur ()
                mLocationLive.value = location
                locationManager!!.removeUpdates(pr)
            }
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        }
        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10.toLong(), 10f, locationListenerChanell)
            locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10.toLong(), 10f,
                    locationListenerChanell)
        }



        return this
    }


    fun removeUpdatesLocationManager(){
        locationManager!!.removeUpdates(locationListenerChanell)
    }


    fun UpdateMarker (googleMap: GoogleMap){
        if (mLocation != null) GoogleMapLoad(googleMap)

    }

    fun TakeDistDur (){

            GlobalScope.launch(Dispatchers.IO) {
                val mPlace = LatLng(mLocation!!.latitude, mLocation!!.longitude)
                val latLngBuilder = LatLngBounds.Builder()
                for (pwt in mPlacesWishTitle) {
                    var dd = TakeDistDur( pwt.place , mPlace)
                    pwt.duration = dd.duration
                    pwt.distans = dd.distans
                    pwt.title =dd.adress
                }
                for (j in mPlacesWishTitle) {
                    latLngBuilder.include(com.google.android.gms.maps.model.LatLng(j.place.lat, j.place.lng))
                }
                mPlacesWishTitle.add(LatLngWishTitle(mPlace, " Я "))
                mPlacesWishTitle[mPlacesWishTitle.size - 1].distans = 0L
                mPlacesWishTitle[mPlacesWishTitle.size - 1].duration = 0L
                mPlacesWishTitle[mPlacesWishTitle.size - 1].image = R.drawable.google_user_location_lbs
            }

    }


    private fun GoogleMapLoad(googleMap: GoogleMap){
        val markers = arrayOfNulls<MarkerOptions>(mPlacesWishTitle.size)
        val latLngBuilder = LatLngBounds.Builder()
        googleMap.clear()
        for (i in mPlacesWishTitle.indices) {
             markers[i] = MarkerOptions()
                    .position(com.google.android.gms.maps.model.LatLng(mPlacesWishTitle[i].place.lat, mPlacesWishTitle[i].place.lng))
            markers[i]?.let { marker ->
                marker.title(mPlacesWishTitle[i].title)
                marker.snippet(mPlacesWishTitle[i].getSnippet())
                mPlacesWishTitle[i].image?.let { markers[i]!!.icon(BitmapDescriptorFactory.fromResource(it)) }
                latLngBuilder.include(com.google.android.gms.maps.model.LatLng(mPlacesWishTitle[i].place.lat, mPlacesWishTitle[i].place.lng))
                googleMap.addMarker(marker)
            }
        }
        val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, mWidth, mWidth, 25)
        googleMap.moveCamera(track)
    }

    fun TakeDistDur(placeFrom: LatLng , placeTo: LatLng): DistDur {
        val mapsApiKey = mContext!!.resources.getString(R.string.google_maps_key)
        val geoApiContext = GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build()
        var result: DirectionsResult? = null

        var durationAll = 0L
        var distanceAll = 0L
        var adres = ""
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.WALKING)
                    .origin(placeFrom)
                    .destination(placeTo).await()
            val legs = result.routes[0].legs
            for (lg in legs) {
                durationAll = (durationAll
                        + lg.duration.inSeconds)
                distanceAll = distanceAll + lg.distance.inMeters
            }
            adres = legs[0].startAddress
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return DistDur(distanceAll, durationAll , adres)
    }


    class DistDur(var distans:Long , var duration: Long  , var adress:String )

    class LatLngWishTitle(var place: LatLng ,var title: String){
        var distans:Long?  = null
        var duration: Long? = null
        var image: Int? = null

        fun getSnippet():String{
            var mResult: StringBuilder  = java.lang.StringBuilder("")
            distans?.let { di ->
                val distanceKM = Math.floor(di.toDouble() / 1000).toLong()
                if (distanceKM == 0L) {
                    mResult = mResult.append("Расстояние " +  DecimalFormat("#").format(di) + " м ")
                } else  mResult = mResult.append("Расстояние " + DecimalFormat("#").format(distanceKM) + "км "
                        + DecimalFormat("#").format((di - distanceKM * 1000)) + " м ")
            }
            duration?.let{du ->
                val minu = Math.floor(du.toDouble() / 60).toLong()
                if (minu == 0L) {
                    mResult = mResult.append("Время меньше минуты" )
                }
                else {
                    val hour = Math.floor(minu.toDouble() / 60).toLong()
                    if (hour ==0L) mResult = mResult.append(";Время " + DecimalFormat("#").format(minu) + " м." )
                    else mResult = mResult.append(";Время " + DecimalFormat("#").format(hour) + " ч. "  +
                            DecimalFormat("#").format( minu - hour * 60) + " м." )
                }
            }

            return mResult.toString()


        }
    }

}
