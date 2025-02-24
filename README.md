# GitHub Scoring algorithm 
## Overview 

The objective of this project is to implement a backend application for scoring repositories on GitHub. This application calls the GitHub API to fetch repositories based on the input provided and calculates scores.
Documentation from GitHub can be found [here](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories)
## Features
* Calls the GitHub API to fetch necessary data. The required query parameters are creationDate (LocalDateTime) and language(String).
* Implements a scoring algorithm to compute scores based on fetched data.

## Few considerations
While fetching the data based on created date, Consider the following:
* In the current implementation whenever a creation date and language is passed all repositories that are greater than the 
created date are fetched. Since there are can be many repositories created this call can quite some time. 
* There is a limitation for number of results fetched in one api call the limit is 1000. To overcome this I have implemented call on hourly
basis until all results for the given created date and language are fetched. 

## Requirement
* Java 21 
* gradle
* Docker (optional, for containerized execution)

# Docker Usage 
## Build docker image 
`docker build -t <containername>`
# Run docker container
`docker run -p 8080:8080 <containername>`
## Configuration
The application retrieves data from GitHub using an API key stored in the application.properties file. Ensure you set the following property before running the application:
`github.api.key=your_api_key_here`

## Example api call 
`http://localhost:8080/api/github/repositories/score?creationDate=2025-02-23T12:00&language=Java`

## Example result 
`[
{
"name": "YiPuzzles",
"score": 102.0
},
{
"name": "Interview-Management-System",
"score": 101.0
},
{
"name": "Data-Struct-Code",
"score": 102.0
},
{
"name": "Data_Structures_and_Algorithms_Using_Core_Java",
"score": 102.0
},
{
"name": "SpringStudentGrade",
"score": 99.0
},
{
"name": "DataOrientedApplicationDesign",
"score": 99.0
},
{
"name": "CalculatingJourney",
"score": 99.0
},
{
"name": "AplikasiPenghitungKata",
"score": 99.0
},
{
"name": "PrdocutoEmpresa",
"score": 99.0
},
{
"name": "WeatherSDK",
"score": 99.0
},
{
"name": "oibsp_task2",
"score": 99.0
},
{
"name": "Demo",
"score": 99.0
},
{
"name": "AstonHW1",
"score": 99.0
},
{
"name": "ftgo-application-sample",
"score": 99.0
},
{
"name": "Tucil1_13523132",
"score": 100.0
}
]`