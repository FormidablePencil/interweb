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

                // todo - do something about these comments
                // each device will have their own unique refresh token by adding a UUID...
                // refresh access-token -> updates the expiration only to the refresh-token that corresponds header.id with token.id
                // and updates in db and returns it to client
                // Since every device has a unique refresh-token, the devices will not have access anymore when both tokens expire
                // and when refreshing access-token, all the other tokens are not updated, the refresh-token in db with id corresponding
                // with the provided valid refresh-token id value which we put in the beginning for this purpose

### Models/tables

- tokens - authorization purposes
- author - resource association purposes
- password - authentication purposes
- password_reset_code

### Why

- we need to restrict resources and modification privileges to everyone outside
- once this Authorization is built, we can have users use the app while new features are still being built
