#!/usr/bin/env bash
set -euo pipefail

# Repository settings are global; they do not belong to the checked-out branch.
repo='NaXeMate/Trainer-Forge'
script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
repo_root=$(cd "$script_dir/.." && pwd)
mode=${1:-preview}

usage() {
  cat <<'USAGE'
Usage: bash scripts/github-security.sh [preview|status|enable-features]

preview          Read current settings and preview gh-secure changes (default).
status           Read gh-secure status and secret scanning settings.
enable-features  Enable secret scanning, push protection, Dependabot and private
                 reporting; verify every enabled setting through the API.

Install the pinned gh-secure extension:
  gh extension install GitHubSecurityLab/gh-secure --pin a04d1c144f5d1b63a8aa5cd7946e8ceafa709a2a
CodeQL uses the committed workflow; main protection has a separate reviewed JSON.
USAGE
}

case "$mode" in
  -h|--help) usage; exit 0 ;;
  preview|status|enable-features) ;;
  *) usage >&2; exit 2 ;;
esac
if [ "$#" -gt 1 ]; then
  usage >&2
  exit 2
fi

command -v gh >/dev/null || { echo 'GitHub CLI (gh) is required.' >&2; exit 1; }
gh secure --help >/dev/null || {
  echo 'Install gh-secure with the pinned installation command shown by this script --help.' >&2
  exit 1
}

# Fail before any mutation if the authenticated account cannot administer this repo.
can_admin=$(gh api "repos/$repo" --jq '.permissions.admin')
if [ "$can_admin" != true ]; then
  echo "Admin access to $repo is required for this setup." >&2
  exit 1
fi

case "$mode" in
  preview)
    echo "Target: $repo (no repository settings will be changed)"
    echo 'Secret scanning prerequisite to apply before gh-secure:'
    cat "$repo_root/.github/security/secret-scanning.json"
    gh secure --repo "$repo" --yes --dry-run secret-scanning dependabot vulnerability-reporting
    echo 'CodeQL: .github/workflows/codeql.yml (advanced setup).'
    echo 'Main protection: .github/security/main-protection.json (apply after CI succeeds).'
    ;;
  status)
    gh secure status --repo "$repo"
    gh api "repos/$repo" --jq '.security_and_analysis'
    echo 'gh-secure checks CodeQL default setup only; inspect Actions for the advanced workflow.'
    ;;
  enable-features)
    gh api "repos/$repo" --method PATCH \
      --input "$repo_root/.github/security/secret-scanning.json" --silent
    gh secure --repo "$repo" --yes secret-scanning dependabot vulnerability-reporting

    scanning=$(gh api "repos/$repo" --jq '.security_and_analysis.secret_scanning.status')
    push_protection=$(gh api "repos/$repo" --jq '.security_and_analysis.secret_scanning_push_protection.status')
    updates=$(gh api "repos/$repo/automated-security-fixes" --jq '.enabled')
    reporting=$(gh api "repos/$repo/private-vulnerability-reporting" --jq '.enabled')
    gh api "repos/$repo/vulnerability-alerts" --silent
    if [ "$scanning" != enabled ] || [ "$push_protection" != enabled ] || \
       [ "$updates" != true ] || [ "$reporting" != true ]; then
      echo 'Verification failed: one or more requested features are not enabled.' >&2
      exit 1
    fi
    echo "Verified: secret scanning, push protection, Dependabot alerts/security updates and private reporting on $repo."
    ;;
esac
