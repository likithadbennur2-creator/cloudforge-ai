#!/bin/bash

set -e

echo "======================================"
echo "        CloudForge CDN Setup"
echo "======================================"

echo "Project ID: ${PROJECT_ID}"

gcloud config set project "${PROJECT_ID}"

BACKEND_SERVICE="cloudforge-backend"

echo "Enabling Cloud CDN..."

gcloud compute backend-services update "${BACKEND_SERVICE}" \
    --project="${PROJECT_ID}" \
    --enable-cdn \
    --global

echo "======================================"
echo "Cloud CDN enabled successfully"
echo "======================================"