package lv.yumm

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lv.yumm.recipes.data.Ingredient

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromList(strings: List<String>): String {
        return gson.toJson(strings)
    }

    @TypeConverter
    fun fromIngredientList(strings: List<Ingredient>): String {
        return gson.toJson(strings)
    }

    @TypeConverter
    fun toList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toIngredientList(json: String): List<Ingredient> {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(json, type)
    }
}