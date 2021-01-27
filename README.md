
### Objective
A service which reads an internally stored log file on the startup. Once started, it renders the data on a REST endpoint.

# Render Logs from internal resource to the server
Java (**openjdk@11**) Spring Boot based Application which has following features based on requirements:
- Given file is assumed to have <1000 lines
- Reads logs from a log file *on the startup*
- Outputs logs  (file content) on the server as soon as the request comes
- Requests are mapped to "/logs" -> *curl http://locahost:8080/logs*
- Configurable parameters to read logs from different files without touching code itself
- Sample file used: [sample logs](https://github.com/Prak2210/RenderLogs/blob/main/src/main/resources/logs)

### Key Highlights
- Application takes 3 parameters from application.yaml (configuration) file which gives developer more control towards changing resource information.
- In case of a "FileNotFound", application outputs "defaultMessage" provided by developers in config file.
- Application leverages Spring Boot Framework and capabilities of annotators provided by it
- path: "/directory1/" and "/directory1" both work.

### Points Covered:
- Getting started in your local
- Implementation & Testing
-- How configuration settings gives more control over the application
- Future Improvement

### Getting started in your local
```
Requirements:
- Java (openJDK@11)
    - if you have mac, try this: brew install --cask adoptopenjdk11
- Gradle command
    - if mac, try this: brew install gradle
```
#### Step 1. Clone the Repo
```
> git clone https://github.com/Prak2210/RenderLogs.git
```
#### Step 2. Start Application by cloned repo

##### Option A: Build using gradle and run a jar
```
> cd renderlogs
> gradle clean build
> java -jar ./build/libs/renderlogs-0.1.jar
```
##### Option B: If you use intelliJ
```
1. Open project in intelliJ
2. open build.gradle
3. Right click and select, "import gradle project" -> intelliJ will index your files
4. Once done, run the com.assessment.logs.RenderLogsApplication java file to start the App
```
##### Option C: Use the Jar Provided
```
"cd" to the same location where you placed the jar and run
> java -jar <jar_name>.jar
```
#### Screenshot of Running a Jar from CMD
<img width="1420" alt="Screen Shot 2021-01-25 at 12 49 08 AM" src="https://user-images.githubusercontent.com/20255532/105668118-afb10f80-5eaa-11eb-8b74-03a5f7b59089.png">

#### Screenshot of Output from Terminal
![Screen Shot 2021-01-26 at 6 21 09 PM](https://user-images.githubusercontent.com/20255532/105919287-5c9ea000-6003-11eb-98b5-d0f52b937eba.png)

#### 3. See Output
- From terminal, do following:
 ```
 > curl http://localhost:8080/logs
 ```
- You can see your logs at this location
- Monitor the logs in application console, you can see request numbers as well.

### Implementation Strategy

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
#### application.yaml
Here, application yaml works as our default configuration file. You can load these configs at compile time:
```
resource:
  name: logs
  path: /
  defaultMessage: Logs Not Found
```
#### RenderLogsApplication
RenderLogs Application is a main Spring boot class for my application which is used to start the application and also, using the @SpringBootApplication annotation it enables auto configs, components scanning and allows extra beans to register.
- It logs "configurations loaded" in the console once compilation done successfully.

#### LogReadConfig
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
- @Bean method getResourceInfo gets instantiated and assembled by Spring IoC at the scan time. Method instantiates ResourceInfo object with parameters passed from yaml configs.
- Once instantiated ResourceInfo, it calls read() on it and returns the object.

#### ResourceInfo
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
- After assigning resourceName and resourcePath can't be modified as they are declared as **final** variables.
- Note: this variables are set as private. Hence, can't be accessed outside *except the "getters"*. Which is helpful because it prevents us from having any code which can violate these values.
- Note: this.logs is **not final** and also, defaultMessage is assigned to it in the constructor. Hence, after calling read() method this.logs value can be changed.
- You can get logs, resource name and path anywhere where you can access this class.

###### Read function
This is one of the main functions of the application.
```
For example, 
-resources/
    - logs/
         - file.txt
so our path will be "/logs/"

read() {
    1. reads file as an inputStream from resourcePath + "/" + resourceName
    2. copies stream to byte array and converts byte (non-unicode to unicode charsets)
    3. if File is not found, it catches the exception and logs the message in console.
    4. If file is found, this.logs gets returned with the new value, else this.logs gets returned with defaultMesage.
}
```


#### RenderLogsController
It is our controller of the application. It contanins a method called renderLogs().
```
    @Autowired
    public RenderLogsController(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }
```
Here, Spring by using @Autowired, it autowires the instance of resourceInfo object from other bean to the constructor.

```
    @RequestMapping("logs")
    @ResponseBody
    public String renderLogs() {
```
- Here, due to request mapping, any request comming to *http://localhost:8080/logs* will be redirected to this method and it will provide the response body. In our case, we just get the logs of resourceInfo object.
- Also, I am using *static* variable resourceNumber to count the total requests so far and it will be shared between all objects.

### Testing Strategy
Below section will mention, what different scenarios and edge cases are considered and tested.

#### ResourceInfoTest
- All the tests added here check these scenarios:
  -- gets default message if file not found or some exception occurs
  -- verifies if it works with an extra trailing "/" in path
  -- verifies if it works when file is under multilevel hierarchy of directories
  -- verifies if these logs returned, *preserve* the formatting or not. example: "\n" gets preserved

#### LogReadConfigTest
With @ActiveProfiles and @ContextConfiguration, these tests set "application-test.yaml" as an active profile to override configurations

the test here checks this scenario:
- verifies that our bean method getResourceConfig() is able to read yaml configurations properly when started

#### RenderLogsControllerTest

all the tests here check these scenarios:
- verifies that if resource name and path are not given, default message gets rendered
- verifies that if correct resource name and path are given, file contents get rendered

#### RenderLogsApplicationTests
Basic unit test which loads "application-test.yaml" as an activeProfile and verifies if returned object for renderLogsController is not Null.
