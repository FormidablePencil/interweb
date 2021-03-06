package com.idealIntent.services

import com.idealIntent.repositories.AppSettingsRepository

class AccountSettingsService(
    private val appSettingsRepository: AppSettingsRepository
) {
    fun GetSettings(authorId: Int) {
        appSettingsRepository.GetSettings(authorId)
    }
}