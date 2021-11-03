# Authorization features

- authorize restricted data with valid token from bearer
- login to get access token (and refresh token)
- reset password to get access token
- create account to get access token

### Token

- tokens must be saved in db
- tokens must be sent from the client via bearer
- and validated everytime before giving access to data
- The refresh token should be stored to validate that the user has not reset their password and wiped all access from
  all devices

### Models/tables

- tokens - authorization purposes
- author - resource association purposes
- password - authentication purposes
- password_reset_code

### Why

- we need to restrict resources and modification privileges to everyone outside
- once this Authorization is built, we can have users use the app while new features are still being built
