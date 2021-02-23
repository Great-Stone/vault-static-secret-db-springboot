# Vault DB Secret - Spring boot Sample

## Vault Policy

```bash
$ vault policy write database-policy - << EOF
# Mount secrets engines
path "sys/mounts/*" {
  capabilities = [ "create", "read", "update", "delete", "list" ]
}

# Spring Vault default 'secret/{application-name}'
path "secret/my-application" {
  capabilities = [ "create", "read", "update", "delete", "list" ]
}

# Configure the database secrets engine and create roles
path "mssql/*" {
  capabilities = [ "create", "read", "update", "delete", "list" ]
}

# Write ACL policies
path "sys/policies/acl/*" {
  capabilities = [ "create", "read", "update", "delete", "list" ]
}

# Manage tokens for verification
path "auth/token/create" {
  capabilities = [ "create", "read", "update", "delete", "list", "sudo" ]
}
EOF
```

## Vault Token

```bash
$ vault token create -policy=database-policy -period=10m
Key                  Value
---                  -----
token                s.XSoCywwGts47DXZjYxFexi1z
token_accessor       KJnKu7uZ1vfnxyhXQaDcaqFy
token_duration       10m
token_renewable      true
token_policies       ["database-policy" "default"]
identity_policies    []
policies             ["database-policy" "default"]
```

token enter in `bootstrap.yml`

```yaml
management:
    endpoints:
        web:
            exposure:
                include: refresh
spring:
    cloud.vault:
        host: vault-1
        port: 8200
        scheme: http
        uri: http://172.28.128.21:8200
        connection-timeout: 5000
        read-timeout: 15000
        authentication: TOKEN
        token: ${APP_VAULT_TOKEN:s.ei8BGygI7gxgXDbso}
        config:
            order: -10
            lifecycle:
                enabled: true
                min-renewal: 10s
                expiry-threshold: 1m
                lease-endpoints: Legacy
        kv:
            enabled: false
        database:
            enabled: true
            static-role: true
            role: mssql-static-role
            backend: mssql
            username-property: spring.datasource.username
            password-property: spring.datasource.password
```