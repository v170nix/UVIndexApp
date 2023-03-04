package uv.index.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import uv.index.lib.data.*
import uv.index.lib.domain.UVForecastHoursUseCase
import uv.index.lib.domain.UVIndexRemoteUpdateUseCase
import uv.index.lib.net.CertOkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UVIModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): UVIndexDatabase {
        return Room.databaseBuilder(
            context,
            UVIndexDatabase::class.java, "uvi-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideIndexDao(indexDb: UVIndexDatabase): UVIndexDao {
        return indexDb.getUVIndexDao()
    }

    @Provides
    @Singleton
    fun provideIndexMetaDao(indexDb: UVIndexDatabase): UVMetaDao {
        return indexDb.getUVMetaDao()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): CertOkHttpClient {
        return CertOkHttpClient(
            HttpClientConfig<OkHttpConfig>().apply {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = false
                    })
                }
            }
        ).apply {
            getHttpClient().plugin(HttpSend).intercept { req ->
                val call = execute(req)
                val resp = call.response
                Log.e("NETWORK", "[${resp.status.value}] ${req.url.build()}")
                call

            }
        }
    }

    @Provides
    @Singleton
    fun provideUVIndexApi(client: CertOkHttpClient): UVIndexAPI {
        return UVIndexAPI(
            client = client,
            url = "https://api.sunexplorer.org/",
            path = "uvi/",
            token = "",
        )
    }

    @Provides
    @Singleton
    fun provideUVIndexRepository(
        api: UVIndexAPI,
        indexDao: UVIndexDao,
        metaDao: UVMetaDao
    ): UVIndexRepository {
        return UVIndexRepository(
            api = api,
            dao = indexDao,
            metaDao = metaDao
        )
    }

    @Provides
    @Singleton
    fun provideSkinRepository(preferences: DataStore<Preferences>): UVSkinRepository {
        return UVSkinRepository(
            preferences = preferences
        )
    }

    @Provides
    @Singleton
    fun provideRemoteUpdateUseCase(repository: UVIndexRepository) =
        UVIndexRemoteUpdateUseCase(repository)

    @Provides
    @Singleton
    fun provideForecastHoursUseCase(repository: UVIndexRepository) =
        UVForecastHoursUseCase(repository)

}