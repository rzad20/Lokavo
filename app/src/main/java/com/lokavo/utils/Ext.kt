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
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.widget.TextView
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

fun View.showSnackbar(message: String, actionText: String? = null, action: (() -> Unit)? = null) {
    val duration = if (action != null) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG
    val snackbar = Snackbar.make(this, message, duration)

    if (actionText != null && action != null) {
        snackbar.setAction(actionText) {
            action()
        }
        val primaryColor = context.theme.obtainStyledAttributes(intArrayOf(com.anychart.R.attr.colorPrimary)).getColor(0, 0)
        snackbar.setActionTextColor(primaryColor)
    }
    snackbar.show()
}

fun extractRelevantText(input: String): String {
    val lines = input.lines()
    val paragraphs = mutableListOf<String>()
    var skipNextLine = false

    for (line in lines) {
        if (line.startsWith("##")) {
            skipNextLine = true
        } else if (skipNextLine) {
            skipNextLine = false
        } else {
            paragraphs.add(line.trim())
        }
    }
    val joinedParagraphs = paragraphs.joinToString("\n").split("\n\n")
    return joinedParagraphs.firstOrNull { it.isNotBlank() } ?: "Relevant text not found"
}

fun TextView.animateText(text: String, delay: Long = 1, onComplete: () -> Unit) {
    this.text = ""
    val handler = Handler(Looper.getMainLooper())
    val charArray = text.toCharArray()
    var index = 0

    handler.post(object : Runnable {
        override fun run() {
            if (index < charArray.size) {
                this@animateText.text = this@animateText.text.toString() + charArray[index]
                index++
                handler.postDelayed(this, delay)
            } else {
                onComplete()
            }
        }
    })
}

