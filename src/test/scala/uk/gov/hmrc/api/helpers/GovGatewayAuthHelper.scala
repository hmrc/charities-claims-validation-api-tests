package uk.gov.hmrc.api.helpers

object GovGatewayAuthHelper {
  def loginBody(charId: String): String =
    s""" 
       |{
       |      "credId": "$charId", 
       |      "confidenceLevel": 200,
       |      "credentialStrength": "strong",
       |      "affinityGroup": "Organisation",
       |      "enrolments": [
       |      {
       |        "key": "HMRC-CHAR-ORG",
       |        "identifiers": [
       |        {
       |          "key": "CHARID",
       |          "value": "123456789"
       |        }
       |        ],
       |        "state": "Activated"
       |      }
       |      ]
       |    }
       |""".stripMargin
}
