#!/bin/bash

set -e

echo "======================================"
echo "      CloudForge Load Balancer Setup"
echo "======================================"

echo "Project ID: ${PROJECT_ID}"
echo "Region: ${REGION}"

gcloud config set project "${PROJECT_ID}"

ZONE="${REGION}-a"

INSTANCE_GROUP="cloudforge-group"
HEALTH_CHECK="cloudforge-health-check"
BACKEND_SERVICE="cloudforge-backend"
URL_MAP="cloudforge-url-map"
HTTP_PROXY="cloudforge-http-proxy"
FORWARDING_RULE="cloudforge-forwarding-rule"

echo "Creating health check..."

gcloud compute health-checks create http "${HEALTH_CHECK}" \
    --project="${PROJECT_ID}" \
    --port=80

echo "Creating backend service..."

gcloud compute backend-services create "${BACKEND_SERVICE}" \
    --project="${PROJECT_ID}" \
    --protocol=HTTP \
    --health-checks="${HEALTH_CHECK}" \
    --global

echo "Adding managed instance group to backend..."

gcloud compute backend-services add-backend "${BACKEND_SERVICE}" \
    --project="${PROJECT_ID}" \
    --instance-group="${INSTANCE_GROUP}" \
    --instance-group-zone="${ZONE}" \
    --global

echo "Creating URL map..."

gcloud compute url-maps create "${URL_MAP}" \
    --project="${PROJECT_ID}" \
    --default-service="${BACKEND_SERVICE}"

echo "Creating HTTP proxy..."

gcloud compute target-http-proxies create "${HTTP_PROXY}" \
    --project="${PROJECT_ID}" \
    --url-map="${URL_MAP}"

echo "Creating forwarding rule..."

gcloud compute forwarding-rules create "${FORWARDING_RULE}" \
    --project="${PROJECT_ID}" \
    --global \
    --target-http-proxy="${HTTP_PROXY}" \
    --ports=80

echo "======================================"
echo "Load balancer created successfully"
echo "======================================"