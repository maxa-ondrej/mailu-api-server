# mailu-api-server

The Dockered API server for mailu, easily add it to your docker-compose.yml file:
```yml
  api:
    image: ghcr.io/maxa-ondrej/mailu-api-server:master
    environment:
      # login credentials for API
      - USERNAME=
      - PASSWORD=
      # database configuration (this can be skipped by setting `env_file: mailu.env`)
      - DB_USER=
      - DB_PW=
      - DB_HOST=
      - DB_NAME=
```

## Supported database drivers
- [x] MariaDB
- [ ] Mysql
- [ ] SQLite
- [ ] PostgreSQL

## Client API
Checkout the Java client: https://github.com/maxa-ondrej/mailu-api

## Publishing the API
### Docker network
Simply put both client and API on the same network and use the container name as url.

### Other
Add port binding to the docker configuration.
