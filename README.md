# Render Logs from internal resource to the server


It's a Java (openjdk@11) Spring Boot based Application which has following features:
- Reads logs from a log file at the startup
- Renders logs on the servcer as soon as the request comes
- Requests are mapped to "/logs"
- Based on requiements, I have assumed file size is smaller (<1000 lines)
- Configurable parameters to read logs from different files without touching code itself

# Points covered:
- Objective
- Implementation Strategy
- Testing Strategy
- How to run application in your local?
- Troubleshooting
- Future Improvements

### Objective

A service which read an internally stored log file at startup. Once started, it needs to have a REST endpoint where it should render the data. Given log data file is assumed to have less than 1000 entries.

### Implementation Strategy

To get started with this, I had to clear few questions for requirements.
- I wanted to confirm what exactly "startup" means?
  -- Should it read data at compile time and render that while running?
  -- Or when started, it reads data line by line and renders it?
- Is there any specific requirements for Java version?
- What should be rendered in case of log data file isn't available?

For this, the final requirements I received are:
- Any Java version
- read at compile time and render while running
- any default message

#### Java Spring Boot Framework

Reason I chose Spring Boot for this was, I would not have to waste a lot of time configuring the environment. It created all for me. I just had to worry about configurations and tests. Also, Annotations by spring make loading env variables and configuring unit tests very easy. Application requirements are smaller right now but considering if in future, we need more features we can leverage other Spring Boot capabilties.

#### Structure
Application has following structure:
- All packages under com.assessment.logs
```
java
    - com.assessment.logs
        - composition
            - ResourceInfo
        - config
            - LogReadConfig
        - controller
            - RenderLogController
        - RenderLogsApplication
resources
    - application.yaml
    - logs
```
##### application.yaml
Here, application yaml works as our default configuration file. You can load these configs at compile time:
```
resource:
  name: logs
  path: /
  defaultMessage: Logs Not Found
```
- [Required] resource.name = name of the file you want logs from
- [Required] resource.path = classpath of the file under resources module
- [Optional] resource.defaultMessage = default message to display on server if file is not found

Having these configuration in yaml can be beneficial if you want to change settings but don't want to touch the code. If you have Helm templates or Argo CD, you just need to touch this file to override these values. No need to go through full CI/CD.

##### RenderLogsApplication
RenderLogs Application is a main Spring boot class for my application which is used to start the application and also, using the @SpringBootApplication annotation it enables auto configs, components scanning and allows extra beans to register.
- It logs "configurations loaded" in the console once compilation done successfully.

##### LogReadConfig
It's a component which gets scanned during component scan as part of @SpringBootApplication in RenderLogsApplication.
- it reads the resource.path, resource.name and resource.defaultMessage configs from yaml file.
```
    @Value("${resource.name}")
    private String resourceName;

    @Value("${resource.defaultMessage: No Logs}")
    private String defaultMessage;
```
- Here, resourceName is required to have in yaml file. However, defaultMessage is optional so if not present in YAML, it goes with value "No Logs".

```
@Bean
public ResourceInfo getResourceInfo() throws IOException {
```
- Bean method getResourceInfo gets loaded by Spring IoC at the scan time. It actually instantiates our ResourceInfo object with parameters passed by user/developer.
- Once instantiated ResourceInfo, it calls "read"  on it and returns the object.

##### ResourceInfo
- This class handles reading from log data file
- As mentioned, ResourceInfo gets initialized by bean method in LogReadConfig class
- To initialize this class, it is necessary to have
  -- file path, file name and default message to be displayed.
```
// constructor definition
ResourceInfo(String resourcePath, String resourceName, String defaultMessage) {
        this.resourceName = resourceName;
        this.resourcePath = trimExtraSlash(resourcePath);
        this.logs = defaultMessage;
}
```
- Before assigning var to resourcePath, I am calling "trimExtraSlash" which basically removes the extra "/" if it's there in path.
- After assigning resourceName and resourcePath can't be modified as they are declared as "final" variables.
- Note: this variables are set as private. Hence, can't be accessed outside except the "getters". Which is helpful because it prevents us from having any code which can violate these values.
- Note: this.logs is not "final" and also, defaultMessage is assigned to it in the constructor. Hence, after calling read() method this.logs value can be changed.
- You can get logs, resource name and path anywhere where you can access this class.

###### Read function
This is one of the main functions of the application.
```
For example, 
-resources
    - logs
so our path will be "/logs"

read() {
    1. reads file as an inputStream from resourcePath + "/" + resourceName
    2. copies stream to byte array and converts byte (non-unicode to unicode charsets)
    3. if File is not found, it catches the exception and logs the message in console.
    4. If file is found, this.logs gets returned with the new value, else this.logs gets returned with defaultMesage.
}
```


##### RenderLogsController
It is our controller of the application. It contanins a method called renderLogs().
```
    @Autowired
    public RenderLogsController(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }
```
Here, @Autowired annotator automatically fills the resourceInfo object created by our bean method LogReadConfig.getResourceInfo().

```
    @RequestMapping("logs")
    @ResponseBody
    public String renderLogs() {
```
Here, renderLogs() is mapped to "logs" path. Hence, any request comming to https://localhost:8080/logs will be redirected to this method and it will provide the response body. In our case, we just get the logs of resourceInfo object.

### Testing Strategy
Below section will mention, what different scenarios and edge cases are considered and tested.

##### ResourceInfoTest
- All the tests added here check these scenarios:
  -- gets default message if file not found or some exception occurs
  -- verifies if it works with an extra trailing "/" in path
  -- verifies if it works when file is under multilevel hierarchy of directories
  -- verifies if these logs returned, preserve the formatting or not. example: "\n" gets preserved

##### LogReadconfigTest
With @ActiveProfiles and @ContextConfiguration, these tests set "application-test.yaml" as an active profile to override configurations

the test here checks these scenarios:
- verifies that our bean method getResourceConfig() is able to read yaml configurations properly

##### RenderLogsControllerTest

all the tests here check these scenarios:
- verifies that if resource name and path are not given, default message gets rendered
- verifies that if correct resource name and path are given, file contents get rendered

##### RenderLogsApplicationTests
Basic test which loads "application-test.yaml" as an activeProfile and verifies if returned object for renderLogsController is not Null.
