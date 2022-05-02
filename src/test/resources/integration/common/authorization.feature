@ignore
Feature: Authorization

  Scenario:
    Given url authorizationUrl
    And header clientEnv = ''
    And header clientName = 'Karate'
    And header clientEnv = '14.0.12'
    And request { "username": "#(user)", "password": "#(pass)" }
    When method POST
    Then status 200
    * def jwt = response.data.token
