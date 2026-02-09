package com.bks.pokedex

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokedexApp : Application(), ImageLoaderFactory {

    /**
     * Configuración global de Coil para optimizar la carga de imágenes en toda la aplicación.
     * Implementar ImageLoaderFactory permite centralizar la gestión de caché y rendimiento.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    // Asignamos el 25% de la memoria disponible para el caché de imágenes
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    // Caché de disco de hasta 50MB para persistir imágenes entre sesiones
                    .maxSizePercent(0.02) 
                    .build()
            }
            // Habilita un suave desvanecimiento al cargar las imágenes para mejorar la percepción visual
            .crossfade(true)
            // Optimiza la calidad del bitmap para ahorrar memoria sin sacrificar fidelidad visual en pantallas móviles
            .allowHardware(true)
            // Permite a Coil usar un formato de color más eficiente (RGB565) en dispositivos con poca RAM
            .allowRgb565(true)
            .build()
    }
}
