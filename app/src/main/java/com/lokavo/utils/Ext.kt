package com.lokavo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.TypedValue
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.lokavo.R

fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun View.showSnackbarOnNoConnection(context: Context) {
    if (!context.isOnline()) {
        showSnackbar(context.getString(R.string.no_internet_connection))
    }
}

suspend fun Geocoder.getAddress(
    latitude: Double,
    longitude: Double,
): Address? = withContext(Dispatchers.IO) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { cont ->
                getFromLocation(latitude, longitude, 1) {
                    cont.resume(it.firstOrNull())
                }
            }
        } else {
            suspendCoroutine { cont ->
                @Suppress("DEPRECATION")
                val address = getFromLocation(latitude, longitude, 1)?.firstOrNull()
                cont.resume(address)
            }
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Error getting address", e)
        null
    }
}

fun Context.bitmapFromVector(vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
    vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable?.intrinsicWidth ?: 0,
        vectorDrawable?.intrinsicHeight ?: 0,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable?.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun View.showSnackbar(message: String) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    val snackbarView = snackbar.view
    val context = snackbarView.context
    val typedValue = TypedValue()
    context.theme.resolveAttribute(com.google.android.material.R.attr.snackbarStyle, typedValue, true)
    val backgroundColor = typedValue.data

    snackbarView.setBackgroundColor(backgroundColor)
    snackbar.setTextColor(context.getColor(R.color.white))

    snackbar.show()
}

fun extractRelevantText(input: String): String {
    // Split the input into lines
    val lines = input.lines()
    val paragraphs = mutableListOf<String>()
    var skipNextLine = false

    // Collect paragraphs, skipping lines after headers
    for (line in lines) {
        if (line.startsWith("##")) {
            skipNextLine = true
        } else if (skipNextLine) {
            skipNextLine = false
        } else {
            paragraphs.add(line.trim())
        }
    }

    // Join paragraphs split by empty lines
    val joinedParagraphs = paragraphs.joinToString("\n").split("\n\n")

    // Return the first non-empty paragraph
    return joinedParagraphs.firstOrNull { it.isNotBlank() } ?: "Relevant text not found"
}

