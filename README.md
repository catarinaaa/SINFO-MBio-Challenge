# SINFO-MBio-Challenge

Command-line interface developed for SINFO Mercedes-Benz.io Challenge (Back-End).  
The tool was developed in Java 7 and does not require other libraries beside Java standard libraries. 


* The status is marked as "down" when there's some problem with the service even if it's not critical
* If the status page is down or the connection times out, the status is marked as "unknown"
* The configuration file must be called `config.txt` and has the following format:
```
service_name1|endpoint1
service_name2|endpoint2
```
* The tool is prepared to monitor bitbucket, github and slack 
* The local storage file is called `data.txt`

The solution developed has 3 java files: `tool.java`, `StatusChecker.java` and `LocalStorage.java`.  
`tool.java` handles and validates the user input (commands and options)  
`StatusChecker.java`is a Singleton and handles the monitoring, commands functionalities and stores the services configured  
`LocalStorage.java` handles the reading and writing to the local storage file


### Installation

To compile the code `javac tool.java`

### Usage

To run `java tool command [options]`

### Author

Name: Catarina Brás  
Email: catarinasaomiguel@gmail.com  
College: Instituto Superior Técnico  
Degree: Mestrado em Engenharia Informática e de Computadores  
Year: 1ºano  

