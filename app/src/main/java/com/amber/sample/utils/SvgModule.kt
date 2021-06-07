package com.amber.sample.utils

import android.content.Context
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import java.io.IOException
import java.io.InputStream


@GlideModule
class SvgModule : AppGlideModule() {
    override fun registerComponents(
        context: Context, glide: Glide, registry: Registry
    ) {
        registry
            .register(SVG::class.java, PictureDrawable::class.java, SvgDrawableTranscoder())
            .append(InputStream::class.java, SVG::class.java, SvgDecoder())
    }

    // Disable manifest parsing to avoid adding similar modules twice.
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

/** Decodes an SVG internal representation from an [InputStream].  */
class SvgDecoder : ResourceDecoder<InputStream?, SVG?> {
    override fun handles(source: InputStream, options: Options): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun decode(
        source: InputStream, width: Int, height: Int, options: Options
    ): Resource<SVG?>? {
        return try {
            val svg: SVG = SVG.getFromInputStream(source)
            if (width != SIZE_ORIGINAL) {
                svg.documentWidth = width.toFloat()
            }
            if (height != SIZE_ORIGINAL) {
                svg.documentHeight = height.toFloat()
            }
            SimpleResource<SVG>(svg)
        } catch (ex: SVGParseException) {
            throw IOException("Cannot load SVG from stream", ex)
        }
    }
}

/**
 * Convert the [SVG]'s internal representation to an Android-compatible one ([Picture]).
 */
class SvgDrawableTranscoder : ResourceTranscoder<SVG?, PictureDrawable?> {
    override fun transcode(
        toTranscode: Resource<SVG?>, options: Options
    ): Resource<PictureDrawable?>? {
        val svg: SVG = toTranscode.get()
        val picture: Picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)
        return SimpleResource(drawable)
    }
}