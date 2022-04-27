@integration
Feature: Get Star Wars characters

  Background:
    * def lukeUrl = baseUrl + '/api/v1/characters/1'
    * def darthVaderUrl = baseUrl + '/api/v1/characters/4'
    * def notFoundCharacterUrl = baseUrl + '/api/v1/characters/9999'

    * def characterSchema =
    """
    {
      "name": "#string",
      "height": "#number",
      "mass": "#number",
      "hairColor": "#string",
      "eyeColor": "#string",
      "birthYear": "#string",
      "gender": "#string",
      "createdAt": "#string",
      "updatedAt": "#string"
    }
    """

    * def errorSchema =
    """
    {
      "name": "#string",
      "status": "#number",
      "timestamp": "#string",
      "code": "#number",
      "resource": "#string",
      "detail": "#string",
      "metadata": {
        "X-B3-TraceId": "#string",
        "X-B3-SpanId": "#string"
      }
    }
    """

  Scenario: Get Luke
    Given url lukeUrl
    When method GET
    Then status 200
    And match response == characterSchema
    And match response.name == 'Luke Skywalker'
    And match response.height == 172

  Scenario: Get Luke's dad
    Given url darthVaderUrl
    When method GET
    Then status 200
    And match response == characterSchema
    And match response.name == 'Darth Vader'
    And match response.hairColor == 'none'

  Scenario: Get character with an id that does not exist
    Given url notFoundCharacterUrl
    When method GET
    Then status 404
    And match response == errorSchema
    And match response.name == 'Not Found'
    And match response.status == 404
    And match response.code == 104
    And match response.detail == 'Star Wars character not found'
