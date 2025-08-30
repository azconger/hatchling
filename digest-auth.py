#!/usr/bin/env python3
"""
MongoDB Atlas-style Digest Authentication for StackHawk
Outputs JSON with Authorization header for external command authentication
"""

import sys
import json
import requests
from requests.auth import HTTPDigestAuth

# Atlas-style API credentials
USERNAME = 'mhvtgter'
PASSWORD = '12345678-1234-1234-1234-123456789abc'

def get_digest_auth_header(target_url):
    """
    Perform digest authentication handshake and return StackHawk JSON format
    """
    try:
        # Create digest auth object
        auth = HTTPDigestAuth(USERNAME, PASSWORD)
        
        # Make authenticated request to a protected endpoint
        # This will automatically handle the digest challenge/response
        response = requests.post(
            f"{target_url}/api/v1/thing",
            data={'thingId': 'stackhawk-digest-test'},
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

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(json.dumps({"error": "Usage: python3 digest-auth.py <target-url>"}), file=sys.stderr)
        sys.exit(1)
    
    target_url = sys.argv[1].rstrip('/')
    get_digest_auth_header(target_url)