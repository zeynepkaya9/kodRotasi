#!/bin/bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="/Users/zeynepkaya/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f/bin/mvn"
cd "$(dirname "$0")/backend"
echo "=== KodRotası - Backend ==="
echo "Java: $(java -version 2>&1 | head -1)"
echo "Maven: $($MVN --version 2>&1 | head -1)"
echo "==================================================="
echo "Backend baslatiliyor (port 8080)..."
echo "Ilk seferde bagimliliklar indirilecek (2-5 dk)"
echo ""
$MVN spring-boot:run -Dspring-boot.run.profiles=dev
