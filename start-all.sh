#!/bin/bash
echo "=== KodRotası ==="
echo "Backend + Frontend baslatiliyor..."
echo ""

export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="/Users/zeynepkaya/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f/bin/mvn"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd "$PROJECT_DIR/backend"
echo "[1/2] Backend baslatiliyor (port 8080)..."
$MVN spring-boot:run -Dspring-boot.run.profiles=dev &
BACKEND_PID=$!

sleep 5

cd "$PROJECT_DIR/frontend"
echo "[2/2] Frontend baslatiliyor (port 5173)..."
VITE_USE_MOCK=false npx vite --host &
FRONTEND_PID=$!

echo ""
echo "==================================================="
echo "Backend:  http://localhost:8080"
echo "Frontend: http://localhost:5173"
echo "H2 Console: http://localhost:8080/h2-console"
echo "==================================================="
echo "Durdurmak icin: Ctrl+C"

trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit" INT TERM
wait
