# Weather App
Weather app using OpenWeatherMap API. [GitHub Repo](https://github.com/ConnorLee2/WeatherApp)
## Technologies used
* Android Studio - IDE, develop Android apps on.
* OpenWeatherMap - API, retrieve JSON files containing weather information.
* Volley - grab data from API
* Moshi/gson - read JSON files as objects.
* Weather icons -
[Weather icons PNG](http://fa2png.io/r/weather-icons/),
[Weather icons creator](http://erikflowers.github.io/weather-icons/)

## Explanations
* To use the app, first search your city in the search bar. Then you can swap between current forecast view and
weekly forecast view.
* AppController.java is in its own directory "app".
* Forecast.java, List.java, Main.java, Weather.java, WeeklyForecast.java are stored in "forecast" folder.
* I store a raw citylist.json obtained from [City List](http://bulk.openweathermap.org/sample/city.list.json.gz) and
search that file for city IDs used in the OpenWeatherMap API.