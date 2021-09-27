package configurations

import org.koin.dsl.module
import repositories.AuthorRepository
import repositories.IAuthorRepository

//object DIHelper : KoinComponent {
object DIHelper {
    val CoreModule = module {
        single { AuthorRepository() as IAuthorRepository }
    }
}