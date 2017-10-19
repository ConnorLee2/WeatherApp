# Weather App
Weather app using OpenWeatherMap API. APK available inside the Other folder.
## Technologies used
* [Android Studio](https://developer.android.com/studio/index.html) - IDE used to develop Android apps on.
* [OpenWeatherMap](https://openweathermap.org/) - API used to retrieve JSON files containing weather information.
* [Volley](https://github.com/google/volley) - used to grab data from API.
* [Moshi](https://github.com/square/moshi)/[gson](https://github.com/google/gson) - used to read JSON files as objects.
* Weather icons -
[Weather icons PNG](http://fa2png.io/r/weather-icons/),
[made by erikflowers](http://erikflowers.github.io/weather-icons/).

## Explanations
* To use the app, first search for your city in the search bar. Then, you can swap between the current forecast view and the
weekly forecast view by tapping on the "Current weather forecast" or "Weekly weather forecast".
* AppController.java is in its own directory "app".
* Forecast.java, List.java, Main.java, Weather.java, WeeklyForecast.java are stored in "forecast" folder.
* I store a raw citylist.json obtained from [City List](http://bulk.openweathermap.org/sample/city.list.json.gz) and
search that file for city IDs used in the OpenWeatherMap API.

## Screenshots
![alt text](https://raw.githubusercontent.com/ConnorLee2/WeatherApp/master/Other/Screenshots/default.jpeg "default")
![alt text](https://raw.githubusercontent.com/ConnorLee2/WeatherApp/master/Other/Screenshots/current_forecast.jpeg "current forecast")
![alt text](https://raw.githubusercontent.com/ConnorLee2/WeatherApp/master/Other/Screenshots/weekly_forecast.jpeg "weekly forecast")
![alt text](https://raw.githubusercontent.com/ConnorLee2/WeatherApp/master/Other/Screenshots/settings.jpeg "settings")
