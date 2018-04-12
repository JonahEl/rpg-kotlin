package weft.extensions

import org.json.JSONObject

fun JSONObject.getBoolean(key: String, default: Boolean) : Boolean {
    if(!this.has(key)) return default
    return this.getBoolean(key)
}

fun JSONObject.getString(key: String, default: String) : String {
    if(!this.has(key)) return default
    return this.getString(key)
}