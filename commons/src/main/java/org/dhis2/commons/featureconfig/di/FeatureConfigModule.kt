package org.dhis2.commons.featureconfig.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import org.dhis2.commons.featureconfig.data.FeatureConfigRepository
import org.dhis2.commons.featureconfig.data.FeatureConfigRepositoryImpl
import org.dhis2.commons.prefs.PreferenceProvider

@Module
class FeatureConfigModule {

    @Provides
    fun gsonProvider(): Gson {
        return Gson()
    }

    @Provides
    fun provideRepository(preferenceProvider: PreferenceProvider): FeatureConfigRepository {
        return FeatureConfigRepositoryImpl(preferenceProvider)
    }
}
