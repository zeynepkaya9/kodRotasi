#!/bin/bash
cd "$(dirname "$0")/frontend"
echo "=== Frontend baslatiliyor (port 5173) ==="
echo "Backend baglantisi: http://localhost:8080/api/v1"
echo ""
npx vite --host
