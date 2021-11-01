package shared

const val persistentId = 34
// todo - database will be wiped from time to time. Create an execution that I can run anytime
//  to check if persistentId exist in the database and if not insert a new user and write to
//  GlobalVariable.kt to change persistentId programmatically and spit out the new id. Also
//  when an error occurs concerning no author by id when running repo unit tests, write a suggestion
//  to execute the program. - I could get to know python along the way.

// todo - persist database for recovery purposes