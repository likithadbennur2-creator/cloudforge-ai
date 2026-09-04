#!/bin/bash

set -e

echo "======================================"
echo "      CloudForge Compute Deployment"
echo "======================================"

echo "Project ID: ${PROJECT_ID}"
echo "Region: ${REGION}"
echo "Instance Count: ${INSTANCE_COUNT}"

gcloud config set project "${PROJECT_ID}"

for i in $(seq 1 "${INSTANCE_COUNT}")
do
    INSTANCE_NAME="cloudforge-instance-${i}"

    echo "Creating instance: ${INSTANCE_NAME}"

    gcloud compute instances create "${INSTANCE_NAME}" \
        --project="${PROJECT_ID}" \
        --zone="${REGION}-a" \
        --machine-type="e2-micro"
done

echo "======================================"
echo "Compute deployment completed"
echo "======================================"