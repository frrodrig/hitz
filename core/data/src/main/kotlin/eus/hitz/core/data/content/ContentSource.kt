package eus.hitz.core.data.content

import android.content.res.AssetManager
import javax.inject.Inject

/** Reads raw content files. Paths are relative to `assets/courses/`. */
fun interface ContentSource {
    fun read(path: String): String
}

internal class AssetContentSource @Inject constructor(
    private val assets: AssetManager,
) : ContentSource {
    override fun read(path: String): String =
        assets.open("courses/$path").bufferedReader(Charsets.UTF_8).use { it.readText() }
}
