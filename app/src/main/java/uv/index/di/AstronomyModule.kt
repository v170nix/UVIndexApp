package uv.index.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.arwix.urania.core.ephemeris.Ephemeris
import uv.index.parts.astronomy.data.ephemeris.SunGeocentricEquatorialApparentEphemeris
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SunEphemeris

@Module
@InstallIn(SingletonComponent::class)
object AstronomyModule {

    @SunEphemeris
    @Provides
    fun provideSunEphemeris(): Ephemeris = SunGeocentricEquatorialApparentEphemeris()

}