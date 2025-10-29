```markdown
## Overview

This repository provides instructions to:

- Build and deploy a container image to Google Cloud Run manually.
- Configure Workload Identity Federation (WIF) for GitHub Actions to deploy to Cloud Run without long-lived GCP keys.
- Set up IAM bindings and environment variables for GitHub Actions integration with GCP.

## Prerequisites

- `gcloud` CLI installed and authenticated with permissions for IAM, Cloud Run, Cloud Build, and Service Accounts.
- **Project**: `gcpterraform60827` (replace if different).
- **Region**: `europe-west1` (replace if different).
- **GitHub repository**: `hibareabhijeet/workwithgcpterraform` (update if needed).

---

## Quick Manual Build & Deploy

```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/gcpterraform60827/workwithgcpterraform

# Deploy to Cloud Run
gcloud run deploy workwithgcpterraform \
  --image gcr.io/gcpterraform60827/workwithgcpterraform \
  --platform managed \
  --region europe-west1 \
  --allow-unauthenticated
```

---

## GitHub Actions Setup with WIF

### 1. Create Workload Identity Pool
```bash
export GCP_PROJECT_ID="gcpterraform60827"
export REGION="europe-west1"

gcloud iam workload-identity-pools create github-pool \
  --project="$GCP_PROJECT_ID" \
  --location="global" \
  --display-name="GitHub Actions pool"
```

### 2. Create OIDC Provider
```bash
gcloud iam workload-identity-pools providers create-oidc github-provider \
  --project="$GCP_PROJECT_ID" \
  --location="global" \
  --workload-identity-pool="github-pool" \
  --display-name="GitHub Provider" \
  --issuer-uri="https://token.actions.githubusercontent.com" \
  --attribute-mapping="google.subject=assertion.sub,attribute.repository=assertion.repository" \
  --attribute-condition="attribute.repository=='hibareabhijeet/workwithgcpterraform'"
```

### 3. Create Service Account
```bash
gcloud iam service-accounts create github-deployer \
  --project="$GCP_PROJECT_ID" \
  --display-name="GitHub Actions Cloud Run Deployer"
```

### 4. Get Project Number
```bash
PROJECT_NUMBER=$(gcloud projects describe "$GCP_PROJECT_ID" --format="value(projectNumber)")
echo "Project number: $PROJECT_NUMBER"
```

### 5. Allow WIF Principals to Impersonate Service Account
```bash
gcloud iam service-accounts add-iam-policy-binding \
  github-deployer@"$GCP_PROJECT_ID".iam.gserviceaccount.com \
  --project="$GCP_PROJECT_ID" \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/github-pool/*"
```

### 6. Grant Service Account Required Roles
```bash
# Cloud Run Admin
gcloud projects add-iam-policy-binding "$GCP_PROJECT_ID" \
  --member="serviceAccount:github-deployer@$GCP_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/run.admin"

# Cloud Build Editor
gcloud projects add-iam-policy-binding "$GCP_PROJECT_ID" \
  --member="serviceAccount:github-deployer@$GCP_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/cloudbuild.builds.editor"

# Storage Admin
gcloud projects add-iam-policy-binding "$GCP_PROJECT_ID" \
  --member="serviceAccount:github-deployer@$GCP_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/storage.admin"
```

### 7. Optional: Allow Impersonation of Other Service Accounts
```bash
gcloud iam service-accounts add-iam-policy-binding \
  github-deployer@"$GCP_PROJECT_ID".iam.gserviceaccount.com \
  --project="$GCP_PROJECT_ID" \
  --role="roles/iam.serviceAccountUser" \
  --member="principalSet://iam.googleapis.com/projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/github-pool/*"
```

---

## GitHub Secrets

Set these in your GitHub repository (Settings → Secrets & variables → Actions):

| Name                | Value                                                                 |
|---------------------|-----------------------------------------------------------------------|
| `WIF_PROVIDER`      | `projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/github-pool/providers/github-provider` |
| `WIF_SERVICE_ACCOUNT` | `github-deployer@gcpterraform60827.iam.gserviceaccount.com`         |
| `GCP_PROJECT_ID`    | `gcpterraform60827`                                                  |
```

### Simplifications:
1. Grouped related steps under clear headings for better readability.
2. Removed redundant explanations and inline comments where the command is self-explanatory.
3. Used tables for GitHub secrets to improve clarity.
4. Minimized repetitive text while retaining all necessary details.