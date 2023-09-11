# pagoPA qi alerts management function

This function handle alerts coming from Grafana translating alerts to EventHub events
containing information extracted from fired alerts

## AlertWebhook

HTTP triggered function that handles webhook requests coming from Grafana.

---

## Run locally with Docker

```Shell
docker build -t pagopa-qi-alerts-management-function .
docker run -p 8999:80 pagopa-qi-alerts-management-function
```

## Run locally with Maven

```Shell
mvn clean package azure-functions:run
```

### Populate the environment

The microservice needs a valid `local.settings.json` file in order to be run.

If you want to start the application without too much hassle, you can just copy `local.settings.json.example` with

```shell
cp ./local.settings.json.example ./local.settings.json
```

to get a good default configuration.

If you want to customize the application environment, reference this table:

| Variable name                     | Description                                                                      | type   | default |
|-----------------------------------|----------------------------------------------------------------------------------|--------|---------|
| QI_ALERTS_TX_EVENTHUB_CONN_STRING | EventHub connection string where function connects to send received alert events | string |         |
