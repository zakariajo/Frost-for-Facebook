package com.pitchedapps.frost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.ButterKnife
import com.pitchedapps.frost.utils.bindView
import com.pitchedapps.frost.web.FrostWebView

/**
 * Created by Allan Wang on 2017-06-01.
 */
class WebOverlayActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.toolbar)
    val refresh: SwipeRefreshLayout by bindView(R.id.swipe_refresh)
    val web: FrostWebView by bindView(R.id.frost_webview)

    companion object {
        private const val ARG_URL = "arg_url"
        fun newInstance(context: Context, url: String) {
            val intent = Intent(context, WebOverlayActivity::class.java)
            intent.putExtra(ARG_URL, url)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_overlay)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
    }
}