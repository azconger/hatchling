#!/bin/bash

# HTTP Digest Authentication for StackHawk
# Supports both environment variables and command line arguments:
#   Environment: HAWK_USERNAME, HAWK_PASSWORD, HAWK_URL  
#   Command line: --username, --password, --url (or -n, -p, -u)

set -euo pipefail

# Parse arguments
username="" password="" target_url=""
while [[ $# -gt 0 ]]; do
    case $1 in
        -u|--url) target_url="$2"; shift 2 ;;
        -n|--username) username="$2"; shift 2 ;;
        -p|--password) password="$2"; shift 2 ;;
        -h|--help) 
            cat << 'EOF'
Digest Authentication for StackHawk

Usage: digest-auth.sh [--url FULL_URL] [--username USER] [--password PASS]

Options:
  -u, --url       Target URL with path (e.g. http://localhost:8080/api/v1/user/profile)
  -n, --username  Username (or use HAWK_USERNAME env var)
  -p, --password  Password (or use HAWK_PASSWORD env var)
  -h, --help      Show this help message

Examples:
  # Using environment variables (StackHawk method)
  HAWK_USERNAME="testuser" HAWK_PASSWORD="abcdef12..." HAWK_URL="http://localhost:8080/api/v1/user/profile" ./digest-auth.sh
  
  # Using command line arguments
  ./digest-auth.sh --url http://localhost:8080/api/v1/user/profile --username testuser --password abcdef12...
  
  # Works with any digest auth endpoint
  ./digest-auth.sh --url http://api.example.com/auth/login --username user --password pass

Environment Variables:
  HAWK_URL, HAWK_USERNAME, HAWK_PASSWORD (fallback if CLI args not provided)
EOF
            exit 0 ;;
        *) echo "{\"error\": \"Unknown option: $1\"}" >&2; exit 1 ;;
    esac
done

# Priority: CLI > env vars
username="${username:-${HAWK_USERNAME:-}}"
password="${password:-${HAWK_PASSWORD:-}}"
target_url="${target_url:-${HAWK_URL:-}}"

# Validate required parameters
missing=()
[[ -z "$username" ]] && missing+=("username (--username or HAWK_USERNAME)")
[[ -z "$password" ]] && missing+=("password (--password or HAWK_PASSWORD)")
[[ -z "$target_url" ]] && missing+=("target URL (--url or HAWK_URL)")
if [[ ${#missing[@]} -gt 0 ]]; then
    echo "{\"error\": \"Missing required parameters: ${missing[*]}\"}" >&2
    exit 1
fi

# Create temporary file for curl headers
temp_file=$(mktemp)
trap "rm -f $temp_file" EXIT

# Use curl's built-in digest auth and capture status
http_status=$(curl -o /dev/null -w "%{http_code}" --digest -u "$username:$password" "$target_url" 2>/dev/null)

# If auth succeeded, capture the complete Authorization header
if [[ "$http_status" -lt 400 ]]; then
    # Capture the Authorization header from verbose output and clean it
    auth_header=$(curl -v --digest -u "$username:$password" "$target_url" 2>&1 | 
        grep "^> Authorization:" | 
        sed 's/^> Authorization: //' |
        tr -d '\r\n')
fi

# Check if authentication succeeded (2xx status codes)
if [[ "$http_status" -ge 400 ]]; then
    error_msg="Authentication failed with HTTP $http_status"
    if [[ "$http_status" == "401" ]]; then
        error_msg="$error_msg - Invalid credentials or authentication required"
    elif [[ "$http_status" == "403" ]]; then
        error_msg="$error_msg - Access forbidden"
    fi
    echo "{\"error\": \"$error_msg\"}" >&2
    exit 1
fi

if [[ -z "$auth_header" ]]; then
    echo "{\"error\": \"Failed to get digest authorization\"}" >&2
    exit 1
fi

# Escape quotes and backslashes in auth header for JSON
escaped_auth_header=$(echo "$auth_header" | sed 's/\\/\\\\/g; s/"/\\"/g')

# Output StackHawk JSON format
echo "{\"headers\": [{\"Authorization\": \"$escaped_auth_header\"}], \"cookies\": []}"