package uv.index.features.place.data

import kotlinx.coroutines.flow.StateFlow

interface PremiumRepository {
    val premiumStateFlow: StateFlow<Boolean>
}