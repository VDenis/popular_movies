# Popular Movies, Stage 1-2

### The Movie Database API Key is required.

API key for themoviedb.org must be included with the build.

You can use two ways:

#### 1) Store api keys with help of gradle and the gradle.properties file

Add the following line to [USER_HOME]/.gradle/gradle.properties

*gradle.properties*
```xml
MyTheMovieDBApiToken="XXXXX"
```

#### 2) Store api keys with help of gradle and the system path variable
Add new system PATH variable *THE_MOVIE_DB_API_TOKEN="XXXXX"*:

**For Windows OS:**
* open system
* advanced system settings
* environment variables
* add new variables to the user variables 
