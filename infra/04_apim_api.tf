locals {
  repo_name             = "pagopa-qi-alerts-management-function"
  display_name          = "pagoPA - Quality Improvement API"
  description           = "API for Quality Improvement service"
  path                  = "qi"
  subscription_required = true
  service_url           = null
}

resource "azurerm_api_management_group" "api_group" {
  name                = local.apim.product_id
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.display_name
  description         = local.description
}

resource "azurerm_api_management_api_version_set" "api_version_set" {
  name                = format("%s-${local.repo_name}", var.env_short)
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.display_name
  versioning_scheme   = "Segment"
}


resource "azurerm_api_management_api" "apim_qi_v1" {
  name                  = format("%s-${local.repo_name}", var.env_short)
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  subscription_required = local.subscription_required
  version_set_id        = azurerm_api_management_api_version_set.api_version_set.id
  version               = "v1"
  revision              = "1"

  description  = local.description
  display_name = local.display_name
  path         = local.path
  protocols    = ["https"]
  service_url  = local.service_url

  subscription_key_parameter_names {
    header = "Authorization"
    query  = "auth-key"
  }

  import {
    content_format = "openapi"
    content_value = templatefile("../openapi/openapi.json", {
      hostname = local.apim.hostname
    })
  }
}

resource "azurerm_api_management_product_api" "apim_qi_product_api" {
  api_name            = azurerm_api_management_api.apim_qi_v1.name
  product_id          = local.apim.product_id
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
}

resource "azurerm_api_management_api_policy" "apim_qi_api_policy" {
  api_name            = azurerm_api_management_api.apim_qi_v1.name
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = var.hostname
  })
}

