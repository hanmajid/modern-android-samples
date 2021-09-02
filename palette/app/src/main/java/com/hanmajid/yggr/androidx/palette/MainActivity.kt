package com.hanmajid.yggr.androidx.palette

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var target: com.squareup.picasso.Target
    private lateinit var layoutLightVibrant: LinearLayoutCompat
    private lateinit var layoutVibrant: LinearLayoutCompat
    private lateinit var layoutDarkVibrant: LinearLayoutCompat
    private lateinit var layoutLightMuted: LinearLayoutCompat
    private lateinit var layoutMuted: LinearLayoutCompat
    private lateinit var layoutDarkMuted: LinearLayoutCompat
    private lateinit var textLightVibrant: TextView
    private lateinit var textVibrant: TextView
    private lateinit var textDarkVibrant: TextView
    private lateinit var textLightMuted: TextView
    private lateinit var textMuted: TextView
    private lateinit var textDarkMuted: TextView

    private val imageUrls = listOf(
        "http://placeimg.com/160/120/animals",
        "http://placeimg.com/160/120/arch",
        "http://placeimg.com/160/120/nature",
        "http://placeimg.com/160/120/people",
        "http://placeimg.com/160/120/tech",
        "http://placeimg.com/160/120/grayscale",
        "http://placeimg.com/160/120/sepia",
    )

    private var imageUrlIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLayout()
        setupImage()

        findViewById<Button>(R.id.button_change_image).setOnClickListener {
            imageUrlIndex++
            setupImage()
        }
    }

    /**
     * Initializes all widgets in the layout
     */
    private fun setupLayout() {
        layoutLightVibrant = findViewById(R.id.layout_light_vibrant)
        layoutVibrant = findViewById(R.id.layout_vibrant)
        layoutDarkVibrant = findViewById(R.id.layout_dark_vibrant)
        layoutLightMuted = findViewById(R.id.layout_light_muted)
        layoutMuted = findViewById(R.id.layout_muted)
        layoutDarkMuted = findViewById(R.id.layout_dark_muted)
        textLightVibrant = findViewById(R.id.text_light_vibrant)
        textVibrant = findViewById(R.id.text_vibrant)
        textDarkVibrant = findViewById(R.id.text_dark_vibrant)
        textLightMuted = findViewById(R.id.text_light_muted)
        textMuted = findViewById(R.id.text_muted)
        textDarkMuted = findViewById(R.id.text_dark_muted)
    }

    /**
     * Fetch image from a URL, display them in an [ImageView], and extract its color profiles.
     */
    private fun setupImage() {
        val url = imageUrls[imageUrlIndex % imageUrls.size]
        target = object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                // Set image to ImageView
                findViewById<ImageView>(R.id.image_source).setImageBitmap(bitmap)

                // Extract color profiles
                bitmap?.let {
                    val palette = Palette.from(it).generate()

                    // Light Vibrant
                    setupColorProfile(
                        palette.lightVibrantSwatch,
                        layoutLightVibrant,
                        textLightVibrant,
                    )
                    // Vibrant
                    setupColorProfile(
                        palette.vibrantSwatch,
                        layoutVibrant,
                        textVibrant,
                    )
                    // Dark Vibrant
                    setupColorProfile(
                        palette.darkVibrantSwatch,
                        layoutDarkVibrant,
                        textDarkVibrant,
                    )
                    // Light Muted
                    setupColorProfile(
                        palette.lightMutedSwatch,
                        layoutLightMuted,
                        textLightMuted,
                    )
                    // Muted
                    setupColorProfile(
                        palette.mutedSwatch,
                        layoutMuted,
                        textMuted,
                    )
                    // Dark Muted
                    setupColorProfile(
                        palette.darkMutedSwatch,
                        layoutDarkMuted,
                        textDarkMuted,
                    )
                }
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                findViewById<ImageView>(R.id.image_source).setImageResource(R.color.purple_200)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }
        }
        Picasso.get()
            .load(url)
            .into(target)
    }

    /**
     * Set colors of a color profile.
     */
    private fun setupColorProfile(
        swatch: Palette.Swatch?,
        layoutCompat: LinearLayoutCompat,
        textView: TextView,
    ) {
        layoutCompat.setBackgroundColor(swatch?.rgb ?: ContextCompat.getColor(this, R.color.white))
        textView.setTextColor(swatch?.titleTextColor ?: ContextCompat.getColor(this, R.color.black))
    }
}