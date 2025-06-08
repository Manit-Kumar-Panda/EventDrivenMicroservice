#!/bin/bash
# check-config-server-started.sh

# Optional: install curl if needed
if ! command -v curl > /dev/null; then
  apt-get update -y
  yes | apt-get install curl
fi

echo "Waiting for config server to be ready..."

while true; do
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)
  echo "Result status code: $curlResult"

  if [[ "$curlResult" == "200" ]]; then
    echo "Config server is up!"
    break
  fi

  >&2 echo "Config server is not up yet... waiting."
  sleep 2
done
