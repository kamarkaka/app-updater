# app-updater
checks for software updates

# How to run:
`mvn clean package`

`java -cp .\target\app-updater-1.0.0-jar-with-dependencies.jar com.kamarkaka.appupdater.RunApp --config .\config\config.properties --output .\output\ --send-email`