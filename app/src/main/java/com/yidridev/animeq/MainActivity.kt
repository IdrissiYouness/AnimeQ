package com.yidridev.animeq

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.GsonBuilder
import com.yidridev.animeq.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private final val URL: String = "https://animechan.xyz/api/random"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAnimeQuote()

        binding.generate.setOnClickListener {
            getAnimeQuote()
        }

        binding.copyBtn.setOnClickListener {
            val clipboard : ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData : ClipData = ClipData.newPlainText("CopyQuote",binding.quote.text.toString())
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(this@MainActivity,"The Quote has been Copied :)",Toast.LENGTH_SHORT).show()
        }

        binding.menuDots.setOnClickListener { view ->
            showPopupMenu(view)
        }



    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { item ->
            onMenuItemClick(item)
        }
        popupMenu.show()

    }

    private fun showAboutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("About")
            .setMessage("AnimeQ is a Simple Random Anime Quotes v1.0 \nMade in ❤ by IDRIDEV\n")

        val dialog = builder.create()
        dialog.show()
    }
    /*
    private fun showPrivacyPolicyDialog() {
        val intent = Intent(this, PrivacyActivity::class.java)
        startActivity(intent)
    }
    */

    private fun openInExternalBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun onMenuItemClick(item: MenuItem?) : Boolean {
        return when (item?.itemId) {
            R.id.menu_item_1 -> {
               Toast.makeText(this@MainActivity,"ITEM 1 CLICKED",Toast.LENGTH_SHORT).show()
                showAboutDialog()
                true
            }
            R.id.menu_item_2 -> {
                Toast.makeText(this@MainActivity,"ITEM 2 CLICKED",Toast.LENGTH_SHORT).show()
                val privacyPolicyUrl = "https://sites.google.com/view/animeq-app-privacy-policy/accueil"
                openInExternalBrowser(privacyPolicyUrl)
                true
            }

            else -> false
        }
    }


    private fun getAnimeQuote() {

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        val request = Request.Builder()
            .url(URL)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.message?.let { Log.e("Failed", it) }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                //println(body)

                val gson = GsonBuilder().create()
                val quoteResponse = gson.fromJson(body, RandQuoteModel::class.java)

                runOnUiThread {
                        val _anime = quoteResponse.anime
                        val _character = quoteResponse.character
                        val _quote = quoteResponse.quote

                        binding.apply {
                            anime.text = _anime
                            character.text = _character
                            quote.text = _quote
                        }

                    /*

                    fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            binding.quote.startAnimation(fadeOutAnimation)
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                    binding.quote.startAnimation(fadeInAnimation)

                   */

                }

            }

        })
    }


}