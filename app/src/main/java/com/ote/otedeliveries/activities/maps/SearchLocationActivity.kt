package com.ote.otedeliveries.activities.maps

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.ote.otedeliveries.R
import com.ote.otedeliveries.adapters.AutoCompletePlaceAdapter
import com.ote.otedeliveries.callbacks.AutoCompletePlaceCallback
import com.ote.otedeliveries.utils.makeGone
import com.ote.otedeliveries.utils.makeVisible
import kotlinx.android.synthetic.main.activity_search_location.*
import kotlinx.android.synthetic.main.connectivity_failure_dialog.view.*
import kotlinx.android.synthetic.main.content_search_location.*

class SearchLocationActivity : AppCompatActivity(), AutoCompletePlaceCallback {
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    private lateinit var placesClient: PlacesClient
    private lateinit var token: AutocompleteSessionToken
    private lateinit var places: ArrayList<AutocompletePrediction>
    private lateinit var autoCompletePlaceAdapter: AutoCompletePlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_location)

        alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(true)
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.connectivity_failure_dialog, null)
        alertDialogBuilder.setView(dialogView)
        alertDialog = alertDialogBuilder.create()

        placesClient = Places.createClient(this)
        token = AutocompleteSessionToken.newInstance()
        places = arrayListOf()
        autoCompletePlaceAdapter = AutoCompletePlaceAdapter(this)
        val myLayoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            adapter = autoCompletePlaceAdapter
            layoutManager = myLayoutManager
        }

        etSearch.addTextChangedListener { text ->
            if (text.toString().isNotBlank()) {
                ivClear.makeVisible()
                geoCodeLocation(text.toString())
            } else {
                ivClear.makeGone()
                places.clear()
                autoCompletePlaceAdapter.swapData(places, "")
            }
        }

        ivClear.setOnClickListener {
            etSearch.setText("")
        }

        etSearch.requestFocus()
        val imm: InputMethodManager? = getSystemService(Context.INPUT_METHOD_SERVICE)
                as? InputMethodManager
        imm?.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)

        ivBack.setOnClickListener {
            finish()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                geoCodeLocation(etSearch.text.toString())
                val mView = this.currentFocus
                if (mView != null) {
                    imm?.hideSoftInputFromWindow(mView.windowToken, 0)
                }
            }
            true
        }

        dialogView.tvOk.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun geoCodeLocation(address: String) {
        val request = FindAutocompletePredictionsRequest.builder()
                .setCountry("ke")
                .setSessionToken(token)
                .setQuery(address)
                .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            places.clear()
            places.addAll(response.autocompletePredictions)
            autoCompletePlaceAdapter.swapData(places, address)
        }.addOnFailureListener { exception ->
            places.clear()
            autoCompletePlaceAdapter.swapData(places, "")
            if (exception is ApiException) {
                alertDialog.show()
            }
        }
    }


    override fun onClickCallback(item: AutocompletePrediction) {
        val myIntent = Intent()
        myIntent.putExtra("placeId", item.placeId)
        setResult(Activity.RESULT_OK, myIntent)
        finish()
    }
}
