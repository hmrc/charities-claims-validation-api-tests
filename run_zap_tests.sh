echo "Running ZAP tests"

sbt -Dbrowser=remote-chrome -Denvironment=local -Dsecurity.assessment=true clean 'testOnly uk.gov.hmrc.api.specs.*' testReport
