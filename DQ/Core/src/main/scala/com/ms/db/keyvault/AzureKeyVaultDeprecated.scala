package com.ms.db.keyvault

/**
 * Util scala object to access the Azure Key Vault
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import scala.collection.JavaConversions._
import com.microsoft.azure.keyvault.KeyVaultClient
import com.microsoft.rest.credentials.ServiceClientCredentials
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials
import com.microsoft.aad.adal4j.AuthenticationResult
import com.microsoft.aad.adal4j.AuthenticationContext
import com.microsoft.aad.adal4j.ClientCredential
import scala.io.Source
import java.util.Properties
import com.ms.db.util.KeyValParser

object AzureKeyVaultDeprecated {

  def getVal(keyName: String): String =
    {

      val kvc: KeyVaultClient = getAuthenticatedClient()
      val kvcUrl = KeyValParser.kv().getProperty("keyvault")+keyName
      val returnedKeyBundle = kvc.getSecret(kvcUrl)
      val secretValue = returnedKeyBundle.value()
      secretValue
    }

  //Creates the KeyVaultClient using the created credentials.

  def getAuthenticatedClient(): KeyVaultClient = {
    val credentials = createCredentials()
    new KeyVaultClient(credentials)
  }

  /**
   * Creates a new KeyVaultCredential based on the access token obtained.
   * @return
   */
  private def createCredentials(): ServiceClientCredentials =
    new KeyVaultCredentials() {

      //Callback that supplies the token type and access token on request.
      override def doAuthenticate(authorization: String, resource: String, scope: String): String = {
        var authResult: AuthenticationResult = null

        try {
          authResult = getAccessToken(authorization, resource)

        } catch {
          case e: Exception => e.printStackTrace()

        }
        authResult.getAccessToken
      }
    }

  def appProperties: Properties = {

    var properties: Properties = null

    val url = getClass.getResource("/app.properties")
    if (url != null) {
      val source = Source.fromURL(url)

      properties = new Properties()
      properties.load(source.bufferedReader())

    }
    properties
  }

  /**
   * Private helper method that gets the access token for the authorization and resource depending on which variables are supplied in the environment.
   *
   * @param authorization
   * @param resource
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   * @throws MalformedURLException
   * @throws Exception
   */
  private def getAccessToken(authorization: String, resource: String): AuthenticationResult = {

    val clientId: String = appProperties.getProperty("adls-client-id")
    val clientKey: String = appProperties.getProperty("adls-client-secret")

    var result: AuthenticationResult = null
    //Starts a service to fetch access token.

    var service: ExecutorService = null

    try {
      service = Executors.newFixedThreadPool(1)
      val context: AuthenticationContext = new AuthenticationContext(authorization, false, service)
      var future: Future[AuthenticationResult] = null

      //Acquires token based on client ID and client secret.

      if (clientKey != null && clientKey != null) {
        val credentials: ClientCredential = new ClientCredential(clientId, clientKey)
        future = context.acquireToken(resource, credentials, null)
      }
      result = future.get

    } finally service.shutdown()
    if (result == null) {
      throw new RuntimeException("Authentication results were null.")
    }
    result
  }

  def main(args: Array[String]): Unit = {
    println(getVal("ADLSId"))
  }

}