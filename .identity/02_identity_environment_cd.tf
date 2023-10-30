resource "azurerm_user_assigned_identity" "environment_cd" {
  location            = var.location
  name                = "${local.app_name}-cd"
  resource_group_name = data.azurerm_resource_group.identity.name
}

resource "azurerm_federated_identity_credential" "environment_cd" {
  name                = "github-federated"
  resource_group_name = data.azurerm_resource_group.identity.name
  audience            = ["api://AzureADTokenExchange"]
  issuer              = "https://token.actions.githubusercontent.com"
  parent_id           = azurerm_user_assigned_identity.environment_cd.id
  subject             = "repo:${var.github.org}/${var.github.repository}:environment:${var.env}-cd"
}

resource "azuread_directory_role_assignment" "environment_cd_directory_readers" {
  role_id             = azuread_directory_role.directory_readers.template_id
  principal_object_id = azurerm_user_assigned_identity.environment_cd.principal_id
}

resource "azurerm_role_assignment" "environment_cd_subscription" {
  for_each             = toset(var.environment_cd_roles.subscription)
  scope                = data.azurerm_subscription.current.id
  role_definition_name = each.key
  principal_id         = azurerm_user_assigned_identity.environment_cd.principal_id
}

resource "azurerm_role_assignment" "environment_cd_tfstate_inf" {
  scope                = data.azurerm_storage_account.tfstate_inf.id
  role_definition_name = "Storage Blob Data Contributor"
  principal_id         = azurerm_user_assigned_identity.environment_cd.principal_id
}

output "azure_environment_cd" {
  value = {
    app_name       = "${local.app_name}-cd"
    client_id      = azurerm_user_assigned_identity.environment_cd.client_id
    application_id = azurerm_user_assigned_identity.environment_cd.client_id
    object_id      = azurerm_user_assigned_identity.environment_cd.principal_id
  }
}
