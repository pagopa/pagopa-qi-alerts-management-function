locals {
  product = "${var.prefix}-${var.env_short}"

  apim = {
    name       = "${local.product}-apim"
    rg         = "${local.product}-api-rg"
    product_id = "qi"
    hostname   = "api.${var.apim_dns_zone_prefix}.${var.external_domain}"
  }
}

