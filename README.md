# Microservice Testing

The aim of this repository is to provide an environment that I can test 
microservices in that is as close as possible to a production deployment
environment while still being fully locally hosted. This should include full
CORS and SSL setup for each service and OAuth2 authentication with Keycloak.

## Keycloak setup

Start the keycloak container from the docker compose file and access the admin
console at [http://localhost:7080](http://localhost:7080) login with the
default credentials `admin/admin`.

```shell
docker compose up -d
```

### Creating a Realm

1. Select "Manage Realms" in the top left
2. Click "Create Realm"
3. Enter `spring-boot-demo` as the name for the realm
4. Click "Create"

### Creating a Client

1. Select "Clients" in the top left.
2. Click "Create".
3. Set the Client ID to `spring-boot-app`.
4. Enable the "Client authentication" toggle.
5. Set Valid redirect URIs to `http://localhost:7080/*`.
6. Set Web origins to `http://localhost:7080`.

### Creating Roles and Users

1. Go to "Realm Roles" and create the roles `user` and `admin`.
2. Go to the "Users" tab and create a test user.
3. Set credentials in the "Credentials" tab.
4. Assign roles in the "Role mapping" tab.

### Creating the test realm

1. Create a new realm with the name `test-realm`.
2. Create a new client with the name `test-client`.
3. Set Valid Redirect URIs to `http://localhost:*` as the spring testcontainer keycloak port changes every time.
4. Set Web origins to `http://localhost:*` for the same reason.
5. Create the same roles as above.

### References

- Spring boot & keycloak integration example: https://oneuptime.com/blog/post/2026-02-02-keycloak-spring-boot/view
- Spring HATEOAS example: https://spring.io/guides/tutorials/rest