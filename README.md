# Sample applications for Event Hubs producer and consumer using Flink

This repo contains a `messageflinkproducer` and `messageflinkconsumer` directories. Each folder is its own spring boot application.

## Dependencies

You'll need the following dependencies to compile and deploy the applications.

- An Azure Subscription with owner role access at least on a resource group.
- [Azure CLI.](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)
- A Git client
- A [Docker](https://www.docker.com/products/docker-desktop/) client.


## Compile and Conainerize Producer and Consumer apps.

In a terminal `git clone <github url>` the repo locally and navigate to the messageflinkproducer folder. You'll compile and build a docker container by running a docker build, example:

```
docker build -t messageflinkproducer . 
```

Once the container is created you can move to the messageflinkconsumer and run docker build as well.

```
docker build -t messageflinkconsumer . 
```


## Event Hub Instance

You'll need an Event Hub instance, to go through the process of creating an Event Hub Namespace and then an Event Hub refer to the [official docs](https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-create).

### Create Shared Access Policies

To enable the `messageflinkproducer` and `messageflinkconsumer` applications to interact with the Event Hub, you need to create two shared access policies: one for the `Send` policy and one for the `Listen` policy.

1. Open the Azure portal and navigate to your Event Hub instance.

2. In the left-hand menu, under the "Settings" section, click on "Shared access policies".

3. Click on the "Add" button to create a new shared access policy.

4. Enter a name for the policy, such as "producer-policy", and select the "Send" permission.

5. Click on the "Create" button to create the policy.

6. Repeat steps 3-5 to create another shared access policy for the `messageflinkconsumer` application. Name this policy "consumer-policy" and select the "Listen" permission.

7. Once both policies are created, you will see them listed under the "Shared access policies" section.

8. For each policy, click on the policy name on the left-hand side and select "Copy connection string".

<!-- 9. Paste the connection string for the "producer-policy" into the appropriate configuration file or environment variable for the `messageflinkproducer` application.

10. Similarly, paste the connection string for the "consumer-policy" into the appropriate configuration file or environment variable for the `messageflinkconsumer` application.

Now, the `messageflinkproducer` application can use the "Send" policy to send messages to the Event Hub, and the `messageflinkconsumer` application can use the "Listen" policy to receive messages from the Event Hub. -->

## Azure Application Insights

You'll also need an Azure Application Insights instance to monitor your applications. Follow the steps below to create an Azure Application Insights instance and copy the connection string:

1. Navigate to the [Azure portal](https://portal.azure.com) and sign in with your Azure account.

2. In the left-hand menu, click on "Create a resource".

3. Search for "Application Insights" and select the "Application Insights" result.

4. Click on the "Create" button to start creating a new Application Insights instance.

5. Fill in the required information, such as the name, subscription, resource group, and location for your Application Insights instance.

6. Click on the "Review + create" button and then click on the "Create" button to create the instance.

7. Once the Application Insights instance is created, navigate to the instance in the Azure portal.

8. In the left-hand menu, under the "Settings" section, click on "Connection strings".

9. Copy the connection string for your Application Insights instance.

<!-- 10. Paste the connection string into the appropriate configuration file or environment variable for your applications. -->

For more detailed instructions on creating an Azure Application Insights instance, you can refer to the [official documentation](https://learn.microsoft.com/en-us/azure/azure-monitor/app/create-workspace-resource?tabs=bicep).


## Test Producer and Consumer locally

Test producer and consumer locally to verify resources and connection stings:

### Producer

```
docker run --rm -it -p 8080:8080 -e bootstrap.servers='<Event Hub Host Name>' -e kafka.topic='<Event Hub Name>' -e kafka.topic.connectionstring='<Producer-Policy connection string>' -e APPLICATIONINSIGHTS_CONNECTION_STRING='<App Insights  connection string>'  messageflinkproducer
```

### Consumer

```
docker run --rm -it -e bootstrap.servers='<Event Hub Host Name>' -e kafka.topic='<Event Hub Name>' -e kafka.topic.connectionstring='<Consumer-Policy connection string>' -e APPLICATIONINSIGHTS_CONNECTION_STRING='<App Insights  connection string>' messageflinkconsumer
```

### Send POST request to producer

You can test your setup by sending a post request to your producer endpoint.

```
POST http://localhost:8080/api/v1/message
Content-Type: application/json

{
    "id": 1,
    "message": "Hello, World!",
    "partition":0
}
```

You should see the following output on the consumer container window:

```
1> {"id":1,"message":"Hello, World!","partition":0,"timestamp":"2024-04-21T15:34:17.525Z"}
```

## Azure Container Registry

To deploy your containers into Azure you'll needa an ACR instance. To create the resource refer to the [official documentation](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-get-started-portal?tabs=azure-cli).

Login to your ACR by running `az acr login --name <registry-name>`

## Push your containers to your ACR

To push your producer and consumer containers you'll need to tag them with your ACR FQDN as follows:

### Producer

```
docker tag messageflinkproducer <registry-name>.azurecr.io/messageflinkproducer

docker push <registry-name>.azurecr.io/messageflinkproducer
```

### Consumer

```
docker tag messageflinkconsumer <registry-name>.azurecr.io/messageflinkconsumer

docker push <registry-name>.azurecr.io/messageflinkconsumer
```

## Deploy Producer into Azure App Service

Create a web app on Linux in Azure App Service using your container image.

1. Browse to the Azure portal and sign in.

2. Click the menu icon for Create a resource, select Compute, and then select Web App.

3. When the Web App on Linux page is displayed, enter the following information:

    - Choose your Subscription from the drop-down list.

    - Choose an existing Resource Group, or specify a name to create a new resource group.

    - Enter a unique name for the App name.

    - Specify Docker Container to Publish.

    - Choose Linux as the Operating System.

    - Select Region.

    - Accept Linux Plan and choose an existing App Service Plan, or select Create new to create a new app service plan.

    - Click Next: Docker.
    
    On the Web App page select Docker, and enter the following information:

    - Select Single Container.

    - Registry: Choose your ACR.

    - Image: Select the image created previously, for example: "messageflinkproducer".

    - Tag: Choose the tag for the image; for example: "latest".

    - Startup Command: Keep it blank since the image already has the startup command.

    After you've entered all of the above information, select Review + create.

For more detailed instructions on creating an Azure App Service, you can refer to the [official documentation](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/deploy-spring-boot-java-app-on-linux#create-a-web-app-on-linux-on-azure-app-service-using-your-container-image).

## Configure environment variables

Navigate to the Settings section and select `Environment Variables`.

In the App Settings tab enter the following vriables:

| Variable | Value |
|----------|-------|
| bootstrap.servers | Event Hub Host Name |
| kafka.topic | Event Hub Name |
| kafka.topic.connectionstring | producer-policy connection string |
| APPLICATIONINSIGHTS_CONNECTION_STRING | App Insights connection string |

##

Repeat the previous steps to deploy consumer container