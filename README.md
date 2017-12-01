StreesTest
==========

與壓力測試相關的程式，主要搭配 JMeter 的 Java Request。

由 JMeter 負責掌控程序的控制，如執行緒的數量、迴圈、Delay 等，
以及參數的設定（如 SCU、SCP、FHIR URL 等等）；
實際執行的內容則由此 Project 來提供。


使用方式
----------

- 下載並安裝 JMeter （3.3 or later）
- 將 build 好的 StreesTest.jar 丟到 JMeter 下的 `lib\ext` 目錄中
- 啟動 JMeter
	- 若要建立 / 維護 / 調整測試計畫可以使用 GUI（直接執行 jmeter.bat）
	- 若要實際進行壓測，可參考下列執行語法：
	```
	jmeter -n -t [jmx file] -l [results file] -e -o [Path to output folder]
	```
	範例：
	```
	jmeter -n -t FHIR.jmx -l result.csv -e -o output
	```
	
- 如果要調整 JVM 的參數可以參考 jmeter.bat
- `jmx` 目錄中存放測試計畫


開發人員注意事項
--------------

- 測試用的 class 必須 extends `AbstractJavaSamplerClient`
- 自行修改 `pom.xml` 的 `deploy.target` 參數
- 執行 mvn install
- 重啟 JMeter，然後進行測試
- 在 Java Request 的設定面板中的 Classname 下拉選單指定要用來進行壓力測試的 class

