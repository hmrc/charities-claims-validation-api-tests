lazy val root = (project in file("."))
  .disablePlugins(JUnitXmlReportPlugin) // attempt to fix https://build.tax.service.gov.uk/job/DASS%20Replatform/job/CHARITIES/job/charities-claims-validation-api-tests/1/
  .settings(
    name := "charities-claims-validation-api-tests",
    version := "0.1.0",
    scalaVersion := "3.3.4",
    libraryDependencies ++= Dependencies.test,
    (Compile / compile) := ((Compile / compile) dependsOn (Compile / scalafmtSbtCheck, Compile / scalafmtCheckAll)).value
  )
