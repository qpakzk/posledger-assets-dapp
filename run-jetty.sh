#!/bin/bash

ASSET_PATH="/data/posledger-assets-dapp"

echo "######### maven install ... ##########"
mvn -Dmaven.test.skip=true clean install

echo "######### kill all java processes ... ##########"
pkill -9 -ef posledger-assets-dapp

echo "######### start assets webapp ... ##########"
cd $ASSET_PATH
nohup mvn jetty:run > /dev/null 2>&1 &