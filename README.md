MOAT IoT Application Example
===
This is an example application using MOAT IoT API set.

The application is composed of 3 parts:

 1. An Android project using [MOAT Java](http://dev.yourinventit.com/references/moat-java-api-document)
 2. Server side Javascripts using [MOAT js](http://dev.yourinventit.com/references/moat-js-api-document)
 3. Rails application using [MOAT REST](http://dev.yourinventit.com/references/moat-rest-api-document)

You need to sign up Inventit IoT Developer Network Sandbox Server in order to deploy the application.

(The server runtime environment will be available when [IIDN Command Line Tool](https://github.com/inventit/iidn-cli) is rolled out.)

## Get Started

See [the tutorial](http://dev.yourinventit.com/guides/get-started) to learn more.

## Source Code License

All program source codes except MOAT IoT App Icon files are available under the MIT style License.

The use of IIDN service requires [our term of service](http://dev.yourinventit.com/legal/term-of-service).

Copyright (c) 2013 Inventit Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## MOAT IoT App Icon Files (moat_icon.png,icon.png)
![Attribution-NonCommercial 3.0 Unported](http://i.creativecommons.org/l/by-nc/3.0/88x31.png "Attribution-NonCommercial 3.0 Unported")

## Change History

1.0.5 : January 26, 2013

* Fixes an issue where authentication token is always missing when DELETE method to /sys/auth object is issued (Rails App)
* Fixes an issue where a resource type attribute is not retrieved because of missing 'f' parameter for specifying the attribute (Rails App)
* Fixes an issue where get/put/post/delete cannot be attribute accessors as they are already defined in activeresource/lib/active_resource/custom_methods.rb (Rails App)
* Fixes an issue where the image URL is obtained via an Image object itself rather than the 'get' property of the Image object (Rails App)

1.0.4 : January 16, 2013  

* Fixes an issue where a Moat instance is not shutdown on MoatIoTService is destroyed

1.0.3 : January 13, 2013  

* Fixes the wrong license term in a file
* Fixes a bug where the wrong number of objects to be saved is counted in a server side script
* Fixes the wrong README description regarding library dependency in the Android app
* Updates Rails to 3.2.11 for the [security fix](http://weblog.rubyonrails.org/2013/1/8/Rails-3-2-11-3-1-10-3-0-19-and-2-3-15-have-been-released/)

1.0.2 : January 9, 2013  

* This README.md is updated

1.0.1 : January 9, 2013  

* Removes debug codes
* Adds README.md to each application to describe
* Updates this doc
* Applies format changes to package.json
* Removes the dependency to inventit-dmc-api

1.0.0 : January 5, 2013  
* Initial Release.
