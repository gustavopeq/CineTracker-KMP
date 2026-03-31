# GitHub Secrets Setup

Go to **Settings > Secrets and variables > Actions** in the GitHub repository and add these secrets:

## Signing Secrets

| Secret | How to get it |
|--------|---------------|
| `KEYSTORE_BASE64` | Run `base64 -i your-release.keystore -o keystore.txt` and paste the contents |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | Your signing key alias |
| `KEY_PASSWORD` | Your signing key password |

## App Secrets

| Secret | How to get it |
|--------|---------------|
| `API_KEY` | Your TMDB API key (same as in local.properties) |
| `SENTRY_DSN` | Your Sentry DSN (same as in local.properties) |
| `SENTRY_ORG` | Your Sentry organization slug |
| `SENTRY_PROJECT` | Your Sentry project name |
| `SENTRY_AUTH_TOKEN` | Your Sentry auth token |

## Encoding the Keystore

```bash
# macOS
base64 -i path/to/your-release.keystore -o keystore-base64.txt

# Linux
base64 path/to/your-release.keystore > keystore-base64.txt
```

Copy the full contents of `keystore-base64.txt` into the `KEYSTORE_BASE64` secret. Delete `keystore-base64.txt` after.

## Repository Workflow Permissions

The release workflow creates version bump PRs automatically. For this to work:

1. Go to **Settings > Actions > General > Workflow permissions**
2. Select **Read and write permissions**
3. Save
