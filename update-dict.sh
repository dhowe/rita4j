#!/bin/sh

jshome=../ritajs

cp $jshome/src/rita_dict.js ./src/main/resources/rita/
cp $jshome/src/rita_dict.js ./target/classes/rita/
ls -l ./src/main/resources/rita/rita_dict.js
