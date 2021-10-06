package domainServices

import repositories.AppSettingsRepository

class AccountSettingsDomainService(
    private val appSettingsRepository: AppSettingsRepository
) {
    fun GetSettings(authorId: Int) {
        appSettingsRepository.GetSettings(authorId)
    }
}