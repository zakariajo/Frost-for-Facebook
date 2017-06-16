package com.pitchedapps.frost.injectors

import android.webkit.WebView
import com.pitchedapps.frost.web.FrostWebViewClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.SingleSubject

class JsBuilder {
    private val css = StringBuilder()
    private val js = StringBuilder()

    fun css(css: String): JsBuilder {
        this.css.append(css)
        return this
    }

    fun js(content: String): JsBuilder {
        this.js.append(content)
        return this
    }

    fun build() = JsInjector(toString())

    override fun toString(): String {
        val builder = StringBuilder().append("!function(){")
        if (css.isNotBlank()) {
            val cssMin = css.replace(Regex("\\s+"), "")
            builder.append("var a=document.createElement('style');a.innerHTML='$cssMin';document.head.appendChild(a);")
        }
        if (js.isNotBlank())
            builder.append(js)
        return builder.append("}()").toString()
    }
}

interface InjectorContract {
    fun inject(webView: WebView) = inject(webView, null)
    fun inject(webView: WebView, callback: ((String) -> Unit)?)
}

/**
 * Helper method to inject multiple functions simultaneously with a single callback
 */
fun WebView.jsInject(vararg injectors: InjectorContract, callback: ((Array<String>) -> Unit) = {}) {
    val observables = Array(injectors.size, { SingleSubject.create<String>() })
    Observable.zip<String, Array<String>>(observables.map { it.toObservable() }, { it.map { it.toString() }.toTypedArray() }).subscribeOn(AndroidSchedulers.mainThread()).subscribe({
        callback.invoke(it)
    })
    (0 until injectors.size).asSequence().forEach {
        i ->
        injectors[i].inject(this, { observables[i].onSuccess(it) })
    }
}

fun FrostWebViewClient.jsInject(vararg injectors: InjectorContract, callback: ((Array<String>) -> Unit) = {}) = webCore.jsInject(*injectors, callback = callback)

class JsInjector(val function: String) : InjectorContract {
    override fun inject(webView: WebView, callback: ((String) -> Unit)?) {
        webView.evaluateJavascript(function, { value -> callback?.invoke(value) })
    }
}
