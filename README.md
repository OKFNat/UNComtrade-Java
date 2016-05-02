# UNComtrade-Java
A Java package for accessing the UN Comtrade database.

## Usage

```java
UNComtrade client = new UNComtrade();
client.setPartnerArea("0");
client.setTimePeriod("2012,2013,2014");
client.setTradeFlow("2");
DataSet results = client.retrieve();
for (DataRow row : results) {
  System.out.println("Exports of " + row.getRtTitle() + " in " + row.getYr() + ": US$ " + row.getTradeValue());
}
System.out.println();
System.out.println("Total international trade value 2012–2014: US$ " + results.getSum("TradeValue"));
```

## Dependencies

The following external libraries are needed to use this package:

* commons-beanutils-1.9.2.jar
* commons-codec-1.9.jar
* commons-collections-3.2.1.jar
* commons-logging-1.2.jar
* fluent-hc-4.5.1.jar
* gson-2.4.jar
* httpclient-4.5.1.jar
* httpclient-cache-4.5.1.jar
* httpclient-win-4.5.1.jar
* httpcore-4.4.3.jar
* httpmime-4.5.1.jar
* jna-4.1.0.jar
* jna-platform-4.1.0.jar


## Copyright

All sourcecode is free software: you can redistribute and/or modify it under the terms of the MIT License (see the LICENSE file).

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

Visit [http://opensource.org/licenses/MIT](http://opensource.org/licenses/MIT) to learn more about the MIT License.
