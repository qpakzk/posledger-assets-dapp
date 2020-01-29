#!/bin/bash

ASSET_PATH="/data/posledger-assets-dapp"

echo "######### maven install ... ##########"
mvn -Dmaven.test.skip=true clean install

echo "######### kill all java processes ... ##########"
pkill -9 -ef posledger-assets-dapp

echo "######### start assets webapp ... ##########"
cd $ASSET_PATH
mvn  -DJava.net.preferIPv4Stack=true -Djetty.port=5984 jetty:run 
