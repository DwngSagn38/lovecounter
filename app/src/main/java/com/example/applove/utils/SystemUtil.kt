package com.example.applove.utils

import android.content.Context
import android.content.res.Configuration
import com.example.applove.roomdb.model.LanguageModel
import java.util.Locale

object SystemUtil {
    private var myLocale: Locale? = null
    fun saveLocale(context: Context, lang: String?) {
        setPreLanguage(context, lang)
    }

    fun setLocale(context: Context) {
        val language = getPreLanguage(context)
        if (language != "") {
            changeLang(language, context)
        }
    }

    fun changeLang(lang: String?, context: Context) {
        if (lang.equals("", ignoreCase = true)) return
        myLocale = Locale(lang)
        saveLocale(context, lang)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getPreLanguage(mContext: Context): String? {
        val preferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE)
        return preferences.getString("KEY_LANGUAGE", "")
    }

    fun setPreLanguage(context: Context, language: String?) {
        if (language == null || language == "") {
            return
        } else {
            val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("KEY_LANGUAGE", language)
            editor.apply()
        }
    }

    fun listLanguage(): List<LanguageModel> {
        val list: MutableList<LanguageModel> = ArrayList<LanguageModel>()
        list.add(LanguageModel("English", "en"))
        list.add(LanguageModel("Hindi", "hi"))
        list.add(LanguageModel("Spanish", "es"))
        list.add(LanguageModel("Portuguese", "pt"))
        list.add(LanguageModel("Vietnamese", "vi"))
        list.add(LanguageModel("Japan", "ja"))
        list.add(LanguageModel("German", "de"))
        list.add(LanguageModel("French", "fr"))
        list.add(LanguageModel("Indonesian", "in"))

        return list
    }
}
