package edu.gvsu.cis.cis357project

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.location.Location
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import androidx.navigation.fragment.findNavController
import edu.gvsu.cis.cis357project.dummy.LocationContent
import edu.gvsu.cis.cis357project.dummy.LocationContent.addItem
import com.google.android.libraries.places.api.Places
import java.util.*

val RequestPermissionCode = 1
var mLocation: Location? = null
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private lateinit var mLocationRequest: LocationRequest
var latitude = 0.0
var longitude = 0.0
var time = ""
var date = ""
var featureName = ""
var state = ""
var addressLine = ""

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If you want to initialize the Places SDK, you would do so like this:
        //this.context?.let { Places.initialize(it, "YOUR_KEY_HERE") }

        val updateButton = view.findViewById<Button>(R.id.updatelocation_button)
        val saveButton = view.findViewById<Button>(R.id.savelocation_button)
        val copyButton = view.findViewById<Button>(R.id.copylocation_button)
        val openButton = view.findViewById<Button>(R.id.open_button)

        val latvalue = view.findViewById<TextView>(R.id.latvalue_label)
        val longvalue = view.findViewById<TextView>(R.id.longvalue_label)
        val addvalue = view.findViewById<TextView>(R.id.addressvalue_label)
        val addextravalue = view.findViewById<TextView>(R.id.addressvalue_label2)

        latvalue.text = ""
        longvalue.text = ""
        addvalue.text = ""
        addextravalue.text = ""

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context as Activity)

        // location requests
        mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var mLocationCallback = LocationCallback()

        // permissions check required before location updates can be requested
        if (ActivityCompat.checkSelfPermission(
                this.context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        }

        updateButton.setOnClickListener { v ->
            getLastLocation(latvalue, longvalue, addvalue, addextravalue, mLocationCallback)
        }

        saveButton.setOnClickListener{ v ->
            saveLocation()
        }

        copyButton.setOnClickListener{ v ->
            copyText("$latitude, $longitude")
        }

        openButton.setOnClickListener{ v ->
            if (latitude == 0.0 && longitude == 0.0) {
                addvalue.text = "Tap \"Update Location\" first!"
            } else {
                // creates an Intent that will load a map of the location
                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=${latitude},${longitude}(Currentparkingspot)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
    }

    private fun getAddress(lat: Double, long: Double) {
        // reverse geocode coordinates into an address
        var geoCoder = Geocoder(this.context, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat, long, 3)

        if (address.size > 0) {
            state = address[0].adminArea
            addressLine = address[0].getAddressLine(0)
            featureName = address[0].featureName
        }
    }

    fun copyText(text:String){
        // copy latlong values into clipboard
        val myClipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Location", text)
        myClipboard.setPrimaryClip(myClip)
    }

    private fun saveLocation(){
        // save last location as a history entry
        val item = LocationContent.LocationItem(
            latitude.toString(),
            longitude.toString(),
            date,
            addressLine
        )
        addItem(item)
    }

    fun getLastLocation(latvalue: TextView, longvalue: TextView, addvalue: TextView, addextravalue: TextView, mLocationCallback: LocationCallback) {
        // get the device's last location
        if (ActivityCompat.checkSelfPermission(
                this.context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    // request location update to get most current location
                    LocationServices.getFusedLocationProviderClient(this.context as Activity).requestLocationUpdates(mLocationRequest, mLocationCallback, null)
                    if (location != null) {
                        // retrieve values
                        latitude = mLocation?.latitude!!
                        longitude = mLocation?.longitude!!
                        time = android.text.format.DateFormat.getTimeFormat(this.context).format(location.time)
                        date = android.text.format.DateFormat.getDateFormat(this.context).format(location.time)

                        // Update text on-screen to reflect location info
                        latvalue.text = latitude.toString()
                        longvalue.text = longitude.toString()
                        getAddress(latitude, longitude)
                        addvalue.text = "$addressLine, $state"
                        addextravalue.text = "$featureName"
                    }
                    else {
                        Log.d("CIS357Project", "No location found")
                    }
                }
        }
    }

    private fun requestPermission() {
        // request location permissions
        ActivityCompat.requestPermissions(
            this.context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestPermissionCode
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.action_history) {
            findNavController().navigate(R.id.action_MainFragment_to_HistoryFragment)
            return true
        }
        return false
    }
}