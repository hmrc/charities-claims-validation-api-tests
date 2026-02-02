#!/usr/bin/env bash

WORKSPACE="$(cd "$(dirname "$0")" && pwd)"
export WORKSPACE

echo
echo "============================================================"
echo " Local DAST Test Runner (OWASP ZAP)"
echo "============================================================"
echo
echo "PREREQUISITES:"
echo "  • OWASP ZAP will be started via dast-config-manager"
echo "  • The following repo MUST exist before running this script:"
echo "      ${WORKSPACE}/dast-config-manager"
echo
echo "SETUP REQUIRED (run once or when updates are needed):"
echo "  git clone <git@github.com:hmrc/dast-config-manager.git> dast-config-manager"
echo "  cd dast-config-manager && git pull"
echo
echo "This script will:"
echo "  1. Start a local ZAP instance"
echo "  2. Run API tests through the ZAP proxy"
echo "  3. Stop the ZAP instance when finished"
echo
echo "============================================================"
echo

browser="chrome"
if [ $# -gt 0  ];
then
  browser="$1"
fi

environment="local"

export ZAP_FORWARD_ENABLE="true"
export ZAP_FORWARD_PORTS=$(lsof -i -P | grep LISTEN | grep :$PORT | grep java | awk '{ print $9}' | sed 's/\*://g' | paste -sd " " -)

export ZAP_LOCAL_ALERT_FILTERS="$WORKSPACE/alert-filters.json"

echo "Starting local OWASP ZAP via dast-config-manager..."
(
  cd $WORKSPACE/dast-config-manager
  make local-zap-running
)

echo "Running tests..."
echo "=========================================="
echo "Browser:              ${browser}"
echo "Env:                  ${environment}"
echo "ZAP Proxy Required:   true"
echo "ZAP alert filters:    ${ZAP_LOCAL_ALERT_FILTERS}"
echo "=========================================="


echo "Running tests via ZAP proxy..."
sbt -Dbrowser=$browser -Denvironment=$environment -Dsecurity.assessment=true clean "testOnly uk.gov.hmrc.api.specs.*"

echo "Stopping OWASP ZAP..."
(
  cd $WORKSPACE/dast-config-manager
  make local-zap-stop
)
echo
echo "DAST test run complete."
echo "============================================================"