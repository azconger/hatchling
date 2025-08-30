#!/usr/bin/env python3
"""
MongoDB Atlas-style Digest Authentication for StackHawk
Outputs JSON with Authorization header for external command authentication

Supports both environment variables and command line arguments:
  Environment: HAWK_USERNAME, HAWK_PASSWORD, HAWK_URL
  Command line: --username, --password, --url (or -n, -p, -u)
"""

import sys
import json
import os
import argparse
import requests
from requests.auth import HTTPDigestAuth

def get_digest_auth_header(target_url, username, password):
    """
    Perform digest authentication handshake and return StackHawk JSON format
    """
    try:
        # Create digest auth object
        auth = HTTPDigestAuth(username, password)
        
        # Make authenticated request to the specified endpoint
        # This will automatically handle the digest challenge/response
        response = requests.get(
            target_url,
            auth=auth,
            timeout=10
        )
        
        # Extract the Authorization header that was used
        auth_header = response.request.headers.get('Authorization')
        
        if auth_header:
            # Output in StackHawk's required JSON format
            output = {
                "headers": [
                    {"Authorization": auth_header}
                ],
                "cookies": []
            }
            print(json.dumps(output))
        else:
            print(json.dumps({"error": "No Authorization header generated"}), file=sys.stderr)
            sys.exit(1)
            
    except requests.exceptions.RequestException as e:
        print(json.dumps({"error": f"Failed to authenticate: {e}"}), file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(json.dumps({"error": f"Unexpected error: {e}"}), file=sys.stderr)
        sys.exit(1)

def parse_credentials():
    """
    Parse credentials from command line arguments or environment variables
    Command line arguments take priority over environment variables
    """
    parser = argparse.ArgumentParser(
        description='MongoDB Atlas-style Digest Authentication for StackHawk',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Using environment variables (StackHawk method)
  HAWK_USERNAME="mhvtgter" HAWK_PASSWORD="12345..." HAWK_URL="http://localhost:8080/api/v1/user/profile" python3 digest-auth.py
  
  # Using command line arguments
  python3 digest-auth.py --url http://localhost:8080/api/v1/user/profile --username mhvtgter --password 12345...
  
  # Works with any digest auth endpoint
  python3 digest-auth.py --url http://api.example.com/auth/login --username user --password pass
        """
    )
    
    parser.add_argument('-u', '--url', 
                       help='Target URL with path (e.g. http://localhost:8080/api/v1/auth) (or use HAWK_URL env var)')
    parser.add_argument('-n', '--username', 
                       help='Username/Public Key (or use HAWK_USERNAME env var)')
    parser.add_argument('-p', '--password', 
                       help='Password/Private Key (or use HAWK_PASSWORD env var)')
    
    args = parser.parse_args()
    
    # Priority: Command line > Environment variables
    username = args.username or os.getenv('HAWK_USERNAME')
    password = args.password or os.getenv('HAWK_PASSWORD')
    target_url = args.url or os.getenv('HAWK_URL')
    
    # Validate all required parameters
    missing = []
    if not username:
        missing.append("username (--username or HAWK_USERNAME)")
    if not password:
        missing.append("password (--password or HAWK_PASSWORD)")
    if not target_url:
        missing.append("target URL (--url or HAWK_URL)")
    
    if missing:
        error_msg = f"Missing required parameters: {', '.join(missing)}"
        print(json.dumps({"error": error_msg}), file=sys.stderr)
        sys.exit(1)
    
    return target_url.rstrip('/'), username, password

if __name__ == "__main__":
    target_url, username, password = parse_credentials()
    get_digest_auth_header(target_url, username, password)