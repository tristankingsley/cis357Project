package edu.gvsu.cis.cis357project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.gvsu.cis.cis357project.dummy.LocationContent

class LocationDataViewModel: ViewModel() {

    data class UnitSettings(val lat: String, val long: String, val date: String, val address: String)

    //private var _settings = MutableLiveData<UnitSettings>()
    private var _selected = MutableLiveData< LocationContent.LocationItem>()

    // Declare properties with getters
    //val settings get() = _settings
    val selected get() = _selected

}