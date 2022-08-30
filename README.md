# forgelauncher

forgelauncher is a simple java application that solves one simple problem I faced while developing forge support for [laboratory](https://github.com/mooziii/laboratory)..

## The Problem

The problem is that my application for managing servers ([laboratory](https://github.com/mooziii/laboratory)) is designed to launch a jarfile in the root directory. The problem is that forge 1.17+ creates a start-script that launched a nested jar. This app could be called a 'man in the middle' that is placed inside the root directory of the server and redirects its call to the nested jarfile. (Providing single-jarfile-support for older and modern forge installations)

